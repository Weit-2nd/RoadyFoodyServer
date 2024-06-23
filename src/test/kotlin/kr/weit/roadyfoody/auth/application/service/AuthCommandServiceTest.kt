package kr.weit.roadyfoody.auth.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kr.weit.roadyfoody.auth.domain.SocialAccessToken
import kr.weit.roadyfoody.auth.fixture.TEST_SOCIAL_ACCESS_TOKEN
import kr.weit.roadyfoody.auth.fixture.createTestKakaoUserResponse
import kr.weit.roadyfoody.auth.fixture.createTestSignUpRequest
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.term.service.TermCommandService
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.useragreedterm.service.UserAgreedTermCommandService
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

class AuthCommandServiceTest : BehaviorSpec({
    val authQueryService = mockk<AuthQueryService>()
    val termCommandService = mockk<TermCommandService>()
    val userAgreedTermCommandService = mockk<UserAgreedTermCommandService>()
    val userRepository = mockk<UserRepository>()
    val imageService = mockk<ImageService>()
    val authCommandService =
        AuthCommandService(authQueryService, termCommandService, userAgreedTermCommandService, userRepository, imageService)

    afterTest { clearAllMocks() }

    given("register 테스트") {
        `when`("프로필 이미지가 없으면 ") {
            every { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) } returns createTestKakaoUserResponse()
            every { userRepository.existsBySocialId(any<String>()) } returns false
            every { termCommandService.checkRequiredTermsOrThrow(any<Set<Long>>()) } just runs
            every { imageService.generateImageName(any<String>()) } returns UUID.randomUUID().toString()
            every { userRepository.save(any<User>()) } returns mockk<User>()
            every { userAgreedTermCommandService.storeUserAgreedTerms(any<User>(), any<Set<Long>>()) } just runs
            every { imageService.upload(any<String>(), any<MultipartFile>()) } just runs
            then("이미지를 업로드하지 않고 User 가 생성된다.") {
                authCommandService.register(TEST_SOCIAL_ACCESS_TOKEN, createTestSignUpRequest(profileImage = null))
                verify(exactly = 1) { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) }
                verify(exactly = 1) { userRepository.existsBySocialId(any<String>()) }
                verify(exactly = 1) { termCommandService.checkRequiredTermsOrThrow(any<Set<Long>>()) }
                verify(exactly = 0) { imageService.generateImageName(any<String>()) }
                verify(exactly = 1) { userRepository.save(any<User>()) }
                verify(exactly = 1) { userAgreedTermCommandService.storeUserAgreedTerms(any<User>(), any<Set<Long>>()) }
                verify(exactly = 0) { imageService.upload(any<String>(), any<MultipartFile>()) }
            }
        }

        `when`("프로필 이미지가 있으면") {
            every { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) } returns createTestKakaoUserResponse()
            every { userRepository.existsBySocialId(any<String>()) } returns false
            every { termCommandService.checkRequiredTermsOrThrow(any<Set<Long>>()) } just runs
            every { imageService.generateImageName(any<String>()) } returns UUID.randomUUID().toString()
            every { userRepository.save(any<User>()) } returns mockk<User>()
            every { userAgreedTermCommandService.storeUserAgreedTerms(any<User>(), any<Set<Long>>()) } just runs
            every { imageService.upload(any<String>(), any<MultipartFile>()) } just runs
            then("이미지를 업로드하고 User 가 생성된다.") {
                authCommandService.register(TEST_SOCIAL_ACCESS_TOKEN, createTestSignUpRequest())
                verify(exactly = 1) { authQueryService.requestKakaoUserInfo(any<SocialAccessToken>()) }
                verify(exactly = 1) { userRepository.existsBySocialId(any<String>()) }
                verify(exactly = 1) { termCommandService.checkRequiredTermsOrThrow(any<Set<Long>>()) }
                verify(exactly = 1) { imageService.generateImageName(any<String>()) }
                verify(exactly = 1) { userRepository.save(any<User>()) }
                verify(exactly = 1) { userAgreedTermCommandService.storeUserAgreedTerms(any<User>(), any<Set<Long>>()) }
                verify(exactly = 1) { imageService.upload(any<String>(), any<MultipartFile>()) }
            }
        }
    }
})
