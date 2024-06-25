package kr.weit.roadyfoody.auth.application.service

import com.ninjasquad.springmockk.MockkBean
import io.awspring.cloud.s3.S3Template
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import kr.weit.roadyfoody.auth.exception.UserAlreadyExistsException
import kr.weit.roadyfoody.auth.fixture.TEST_SOCIAL_ACCESS_TOKEN
import kr.weit.roadyfoody.auth.fixture.createTestImageFile
import kr.weit.roadyfoody.auth.fixture.createTestKakaoUserResponse
import kr.weit.roadyfoody.auth.fixture.createTestSignUpRequest
import kr.weit.roadyfoody.global.config.S3Properties
import kr.weit.roadyfoody.support.annotation.ServiceIntegrateTest
import kr.weit.roadyfoody.support.utils.ImageFormat.WEBP
import kr.weit.roadyfoody.term.fixture.createTestTerms
import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.user.fixture.TEST_USER_NICKNAME
import kr.weit.roadyfoody.user.fixture.TEST_USER_SOCIAL_ID
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByNickname
import kr.weit.roadyfoody.useragreedterm.exception.RequiredTermNotAgreedException
import org.springframework.transaction.annotation.Transactional

@Transactional
@ServiceIntegrateTest
class AuthIntegrationServiceTest(
    private val s3Template: S3Template,
    private val s3Properties: S3Properties,
    private val userRepository: UserRepository,
    private val termRepository: TermRepository,
    @MockkBean private val authQueryService: AuthQueryService,
    private val authCommandService: AuthCommandService,
) : BehaviorSpec({
        lateinit var validTermIdSet: Set<Long>
        beforeSpec {
            s3Template.createBucket(s3Properties.bucket)
            validTermIdSet = termRepository.saveAll(createTestTerms()).map { it.id }.toSet()
        }
        beforeEach {
            every { authQueryService.requestKakaoUserInfo(any<String>()) } returns createTestKakaoUserResponse()
        }
        afterEach {
            userRepository.findAll()
                .forEach {
                    it.profile.profileImageName?.let { imageName ->
                        s3Template.deleteObject(s3Properties.bucket, imageName)
                    }
                }
            clearAllMocks()
        }
        afterSpec {
            termRepository.deleteAll()
            s3Template.deleteBucket(s3Properties.bucket)
        }

        given("프로필 사진이 존재하는 경우") {
            `when`("회원가입을 요청하면") {
                then("회원가입이 성공한다") {
                    val signUpRequest = createTestSignUpRequest(termIdSet = validTermIdSet)
                    authCommandService.register(TEST_SOCIAL_ACCESS_TOKEN, signUpRequest, createTestImageFile(WEBP))
                    val profileImageName = userRepository.getByNickname(signUpRequest.nickname).profile.profileImageName
                    profileImageName.shouldNotBeNull()
                    s3Template.objectExists(s3Properties.bucket, profileImageName).shouldBeTrue()
                    verify(exactly = 1) { authQueryService.requestKakaoUserInfo(any<String>()) }
                }
            }
        }

        given("프로필 사진이 존재하지 않는 경우") {
            `when`("회원가입을 요청하면") {
                then("회원가입이 성공한다") {
                    val signUpRequest = createTestSignUpRequest(termIdSet = validTermIdSet)
                    authCommandService.register(TEST_SOCIAL_ACCESS_TOKEN, signUpRequest, null)
                    val profileImageName = userRepository.getByNickname(signUpRequest.nickname).profile.profileImageName
                    profileImageName.shouldBeNull()
                    verify(exactly = 1) { authQueryService.requestKakaoUserInfo(any<String>()) }
                }
            }
        }

        given("필수약관을 동의하지 않은 경우") {
            `when`("회원가입을 요청하면") {
                then("RequiredTermNotAgreedException 을 던진다") {
                    shouldThrow<RequiredTermNotAgreedException> {
                        authCommandService.register(
                            TEST_SOCIAL_ACCESS_TOKEN,
                            createTestSignUpRequest(termIdSet = emptySet()),
                            null,
                        )
                    }
                    verify(exactly = 1) { authQueryService.requestKakaoUserInfo(any<String>()) }
                }
            }
        }

        given("이미 가입된 사용자인 경우") {
            beforeContainer {
                userRepository.save(createTestUser(socialId = TEST_USER_SOCIAL_ID))
            }
            afterContainer {
                userRepository.deleteAll()
            }
            `when`("회원가입을 요청하면") {
                then("UserAlreadyExistsException 을 던진다") {
                    shouldThrow<UserAlreadyExistsException> {
                        authCommandService.register(
                            TEST_SOCIAL_ACCESS_TOKEN,
                            createTestSignUpRequest(termIdSet = validTermIdSet),
                            null,
                        )
                    }
                    verify(exactly = 1) { authQueryService.requestKakaoUserInfo(any<String>()) }
                }
            }
        }

        given("중복된 닉네임인 경우") {
            beforeContainer {
                userRepository.save(createTestUser(nickname = TEST_USER_NICKNAME))
            }
            afterContainer {
                userRepository.deleteAll()
            }
            `when`("회원가입을 요청하면") {
                then("UserAlreadyExistsException 을 던진다") {
                    shouldThrow<UserAlreadyExistsException> {
                        authCommandService.register(
                            TEST_SOCIAL_ACCESS_TOKEN,
                            createTestSignUpRequest(nickname = TEST_USER_NICKNAME, termIdSet = validTermIdSet),
                            null,
                        )
                    }
                    verify(exactly = 1) { authQueryService.requestKakaoUserInfo(any<String>()) }
                }
            }
        }
    })
