package kr.weit.roadyfoody.foodSpots.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kr.weit.roadyfoody.foodSpots.fixture.MockTestFoodSpot
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_LAST_ID
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_PHOTO_URL
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_SIZE
import kr.weit.roadyfoody.foodSpots.fixture.createMockSliceFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodSpotList
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsForDistance
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportFoodCategory
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.getByHistoryId
import kr.weit.roadyfoody.foodSpots.repository.getHistoriesByUser
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
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
            val foodSpotsRepository = mockk<FoodSpotsRepository>()
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
                    foodSpotsRepository,
                )
            val user = createTestUser()
            afterEach { clearAllMocks() }
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

            given("searchFoodSpots 테스트") {
                val query500m =
                    FoodSpotsSearchCondition(
                        centerLongitude = 0.0,
                        centerLatitude = 0.0,
                        radius = 500,
                        name = null,
                        categoryIds = emptyList(),
                    )
                `when`("정상적으로 500m 거리 이내 음식점 검색이 가능한 경우") {
                    every {
                        foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                            0.0,
                            0.0,
                            500,
                            null,
                            emptyList(),
                        )
                    } returns createMockTestFoodSpotList()
                    then("500m 거리 이내 음식점을 반환한다.") {

                        val foodSpotsSearchResponses =
                            foodSPotsQueryService.searchFoodSpots(query500m)
                        foodSpotsSearchResponses.items.shouldHaveSize(3)
                        verify(exactly = 1) {
                            foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                                0.0,
                                0.0,
                                500,
                                null,
                                emptyList(),
                            )
                        }
                    }
                }
                val query1000m =
                    FoodSpotsSearchCondition(
                        centerLongitude = 0.0,
                        centerLatitude = 0.0,
                        radius = 1000,
                        name = null,
                        categoryIds = emptyList(),
                    )
                `when`("정상적으로 10000m 거리 이내 음식점 검색이 가능한 경우") {
                    every {
                        foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                            0.0,
                            0.0,
                            1000,
                            null,
                            emptyList(),
                        )
                    } returns createTestFoodSpotsForDistance()
                    every {
                        userRepository.save(any())
                    } returns createTestUser(coin = 800)

                    then("1000m 거리 이내 음식점을 반환한다.") {
                        val foodSpotsSearchResponses =
                            foodSPotsQueryService.searchFoodSpots(query1000m)
                        foodSpotsSearchResponses.items.shouldHaveSize(5)
                        verify(exactly = 1) {
                            foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                                0.0,
                                0.0,
                                1000,
                                null,
                                emptyList(),
                            )
                        }
                    }
                }

                `when`("정상적으로 카테고리 별 500m 거리 이내 음식점을 반환한다.") {
                    val categoryQuery =
                        FoodSpotsSearchCondition(
                            centerLongitude = 0.0,
                            centerLatitude = 0.0,
                            radius = 500,
                            name = null,
                            categoryIds = listOf(1L, 2L),
                        )
                    every {
                        foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                            0.0,
                            0.0,
                            500,
                            null,
                            listOf(1L, 2L),
                        )
                    } returns listOf(MockTestFoodSpot())

                    then("카테고리 별 500m 거리 이내 음식점을 반환한다.") {
                        val foodSpotsSearchResponses =
                            foodSPotsQueryService.searchFoodSpots(categoryQuery)
                        foodSpotsSearchResponses.items.shouldHaveSize(1)
                        verify(exactly = 1) {
                            foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                                0.0,
                                0.0,
                                500,
                                null,
                                listOf(1L, 2L),
                            )
                        }
                    }
                }
                `when`("반환할 가게가 없는 경우") {
                    every {
                        foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                            0.0,
                            0.0,
                            500,
                            null,
                            emptyList(),
                        )
                    } returns emptyList()
                    then("빈 리스트를 반환한다.") {
                        val foodSpotsSearchResponses =
                            foodSPotsQueryService.searchFoodSpots(query500m)
                        foodSpotsSearchResponses.items.shouldBeEmpty()
                        verify(exactly = 1) {
                            foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                                0.0,
                                0.0,
                                500,
                                null,
                                emptyList(),
                            )
                        }
                    }
                }
            }
        },
    )
