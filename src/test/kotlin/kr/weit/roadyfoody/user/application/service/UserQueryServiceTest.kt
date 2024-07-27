package kr.weit.roadyfoody.user.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.user.fixture.TEST_USER_PROFILE_IMAGE_URL
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import java.util.Optional

class UserQueryServiceTest :
    BehaviorSpec({
        val userRepository = mockk<UserRepository>()
        val imageService = mockk<ImageService>()
        val userQueryService = UserQueryService(userRepository, imageService)

        afterEach { clearAllMocks() }

        given("getUserInfo 테스트") {
            `when`("프로필사진이 존재하는 유저의 경우") {
                val user = createTestUser()
                every { userRepository.findById(any<Long>()) } returns Optional.of(user)
                every { imageService.getDownloadUrl(any<String>()) } returns TEST_USER_PROFILE_IMAGE_URL
                then("프로필사진 URL 이 존재하는 응답을 반환한다.") {
                    val userInfoResponse = userQueryService.getUserInfo(user)
                    userInfoResponse.profileImageUrl shouldBe TEST_USER_PROFILE_IMAGE_URL
                    verify(exactly = 1) { imageService.getDownloadUrl(any<String>()) }
                }
            }

            `when`("프로필사진이 존재하지 않는 유저의 경우") {
                val user = createTestUser(profileImageName = null)
                every { userRepository.findById(any<Long>()) } returns Optional.of(user)
                every { imageService.getDownloadUrl(any<String>()) } returns TEST_USER_PROFILE_IMAGE_URL
                then("프로필사진 URL 이 null 인 응답을 반환한다.") {
                    val userInfoResponse = userQueryService.getUserInfo(user)
                    userInfoResponse.profileImageUrl.shouldBeNull()
                    verify(exactly = 0) { imageService.getDownloadUrl(any<String>()) }
                }
            }
        }
    })
