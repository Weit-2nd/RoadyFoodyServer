package kr.weit.roadyfoody.auth.application.service

import io.awspring.cloud.s3.S3Template
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.auth.domain.SocialAccessToken
import kr.weit.roadyfoody.auth.exception.UserAlreadyExistsException
import kr.weit.roadyfoody.auth.fixture.TEST_SOCIAL_ACCESS_TOKEN
import kr.weit.roadyfoody.auth.fixture.createTestKakaoUserResponse
import kr.weit.roadyfoody.auth.fixture.createTestSignUpRequest
import kr.weit.roadyfoody.global.config.S3Properties
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.support.annotation.ServiceIntegrateTest
import kr.weit.roadyfoody.term.fixture.createTestTerms
import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.term.service.TermCommandService
import kr.weit.roadyfoody.user.fixture.TEST_USER_SOCIAL_ID
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByNickname
import kr.weit.roadyfoody.useragreedterm.exception.RequiredTermNotAgreedException
import kr.weit.roadyfoody.useragreedterm.service.UserAgreedTermCommandService
import org.springframework.transaction.annotation.Transactional

@Transactional
@ServiceIntegrateTest
class AuthIntegrationServiceTest(
    private val s3Template: S3Template,
    private val s3Properties: S3Properties,
    private val termCommandService: TermCommandService,
    private val userAgreedTermCommandService: UserAgreedTermCommandService,
    private val userRepository: UserRepository,
    private val imageService: ImageService,
    private val termRepository: TermRepository,
) : BehaviorSpec({
        lateinit var validTermIdSet: Set<Long>
        beforeSpec {
            s3Template.createBucket(s3Properties.bucket)
            validTermIdSet = termRepository.saveAll(createTestTerms()).map { it.id }.toSet()
        }
        afterEach {
            userRepository.findAll()
                .forEach {
                    it.profile.profileImageName?.let { imageName ->
                        s3Template.deleteObject(s3Properties.bucket, imageName)
                    }
                }
        }
        afterTest { clearAllMocks() }
        afterSpec {
            termRepository.deleteAll()
            s3Template.deleteBucket(s3Properties.bucket)
        }

        val authQueryService: AuthQueryService = mockk<AuthQueryService>()
        val authCommandService =
            AuthCommandService(
                authQueryService,
                termCommandService,
                userAgreedTermCommandService,
                userRepository,
                imageService,
            )

        given("프로필 사진이 존재하는 경우") {
            `when`("회원가입을 요청하면") {
                every { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) } returns createTestKakaoUserResponse()
                then("회원가입이 성공한다") {
                    val signUpRequest = createTestSignUpRequest(validTermIdSet)
                    authCommandService.register(TEST_SOCIAL_ACCESS_TOKEN, signUpRequest)
                    val profileImageName = userRepository.getByNickname(signUpRequest.nickname).profile.profileImageName
                    profileImageName.shouldNotBeNull()
                    s3Template.objectExists(s3Properties.bucket, profileImageName).shouldBeTrue()
                    verify(exactly = 1) { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) }
                }
            }
        }

        given("프로필 사진이 존재하지 않는 경우") {
            `when`("회원가입을 요청하면") {
                every { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) } returns createTestKakaoUserResponse()
                then("회원가입이 성공한다") {
                    val signUpRequest = createTestSignUpRequest(validTermIdSet, profileImage = null)
                    authCommandService.register(TEST_SOCIAL_ACCESS_TOKEN, signUpRequest)
                    val profileImageName = userRepository.getByNickname(signUpRequest.nickname).profile.profileImageName
                    profileImageName.shouldBeNull()
                    verify(exactly = 1) { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) }
                }
            }
        }

        given("필수약관을 동의하지 않은 경우") {
            `when`("회원가입을 요청하면") {
                every { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) } returns createTestKakaoUserResponse()
                then("RequiredTermNotAgreedException 을 던진다") {
                    shouldThrow<RequiredTermNotAgreedException> {
                        authCommandService.register(
                            TEST_SOCIAL_ACCESS_TOKEN,
                            createTestSignUpRequest(termIdSet = emptySet()),
                        )
                    }
                    verify(exactly = 1) { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) }
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
                every { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) } returns createTestKakaoUserResponse()
                then("UserAlreadyExistsException 을 던진다") {
                    shouldThrow<UserAlreadyExistsException> {
                        authCommandService.register(TEST_SOCIAL_ACCESS_TOKEN, createTestSignUpRequest(validTermIdSet))
                    }
                    verify(exactly = 1) { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) }
                }
            }
        }
    })
