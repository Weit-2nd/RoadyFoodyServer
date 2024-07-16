package kr.weit.roadyfoody.foodSpots.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
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
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import java.util.Optional

class FoodSpotsQueryServiceTest :
    BehaviorSpec(
        {
            val foodSpotsHistoryRepository = mockk<FoodSpotsHistoryRepository>()
            val foodSpotsPhotoRepository = mockk<FoodSpotsPhotoRepository>()
            val userRepository = mockk<UserRepository>()
            val reportFoodCategoryRepository = mockk<ReportFoodCategoryRepository>()
            val imageService = spyk(ImageService(mockk()))
            val foodSPotsQueryService =
                FoodSpotsQueryService(
                    userRepository,
                    foodSpotsHistoryRepository,
                    foodSpotsPhotoRepository,
                    reportFoodCategoryRepository,
                    imageService,
                )
            val user = createTestUser()

            given("getReportHistories 테스트") {
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
                        foodSPotsQueryService.getReportHistories(
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
                            foodSPotsQueryService.getReportHistories(
                                TEST_USER_ID,
                                TEST_FOOD_SPOTS_SIZE,
                                TEST_FOOD_SPOTS_LAST_ID,
                            )
                        }
                    }
                }
            }
        },
    )
