package kr.weit.roadyfoody.user.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_LAST_ID
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_PHOTO_URL
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_SIZE
import kr.weit.roadyfoody.foodSpots.fixture.createMockSliceFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportFoodCategory
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.getByHistoryId
import kr.weit.roadyfoody.foodSpots.repository.getHistoriesByUser
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.user.exception.UserNotFoundException
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.TEST_USER_PROFILE_IMAGE_URL
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import java.util.Optional

class UserQueryServiceTest :
    BehaviorSpec({
        val userRepository = mockk<UserRepository>()
        val imageService = mockk<ImageService>()
        val foodSpotsHistoryRepository = mockk<FoodSpotsHistoryRepository>()
        val foodSpotsPhotoRepository = mockk<FoodSpotsPhotoRepository>()
        val reportFoodCategoryRepository = mockk<ReportFoodCategoryRepository>()

        val userQueryService =
            UserQueryService(
                userRepository,
                imageService,
                foodSpotsHistoryRepository,
                foodSpotsPhotoRepository,
                reportFoodCategoryRepository,
            )

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
        given("getReportHistories 테스트") {
            val user = createTestUser()
            every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
            every {
                foodSpotsHistoryRepository.getHistoriesByUser(
                    user,
                    TEST_FOOD_SPOTS_SIZE,
                    TEST_FOOD_SPOTS_LAST_ID,
                )
            } returns createMockSliceFoodHistory()
            every { foodSpotsPhotoRepository.getByHistoryId(any()) } returns
                listOf(
                    createTestFoodSpotsPhoto(),
                )
            every { imageService.getDownloadUrl(any()) } returns TEST_FOOD_SPOTS_PHOTO_URL
            every { reportFoodCategoryRepository.getByHistoryId(any()) } returns
                listOf(
                    createTestReportFoodCategory(),
                )
            `when`("정상적인 데이터가 들어올 경우") {
                then("정상적으로 조회되어야 한다.") {
                    userQueryService.getReportHistories(
                        TEST_USER_ID,
                        TEST_FOOD_SPOTS_SIZE,
                        TEST_FOOD_SPOTS_LAST_ID,
                    )
                }
            }

            `when`("사용자가 존재하지 않는 경우") {
                every { userRepository.findById(TEST_USER_ID) } returns Optional.empty()
                then("UserNotFoundException이 발생한다.") {
                    shouldThrow<UserNotFoundException> {
                        userQueryService.getReportHistories(
                            TEST_USER_ID,
                            TEST_FOOD_SPOTS_SIZE,
                            TEST_FOOD_SPOTS_LAST_ID,
                        )
                    }
                }
            }
        }
    })
