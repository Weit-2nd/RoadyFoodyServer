package kr.weit.roadyfoody.foodSpots.application.service

import TEST_FOOD_SPOT_ID
import TEST_REVIEW_PHOTO_URL
import createMockSliceReview
import createTestReviewPhoto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kr.weit.roadyfoody.foodSpots.exception.FoodSpotsHistoryNotFoundException
import kr.weit.roadyfoody.foodSpots.fixture.MockTestFoodSpot
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_HISTORY_ID
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_PHOTO_URL
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodSpotList
import kr.weit.roadyfoody.foodSpots.fixture.createTestAggregatedInfoResponse
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsForDistance
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportFoodCategory
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportOperationHours
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.getByFoodSpotsId
import kr.weit.roadyfoody.foodSpots.repository.getByHistoryId
import kr.weit.roadyfoody.global.TEST_LAST_ID
import kr.weit.roadyfoody.global.TEST_PAGE_SIZE
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.exception.FoodSpotsNotFoundException
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.ReviewSortType
import kr.weit.roadyfoody.review.repository.getByReview
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import java.util.Optional
import java.util.concurrent.ExecutorService

class FoodSpotsQueryServiceTest :
    BehaviorSpec(
        {
            val foodSpotsHistoryRepository = mockk<FoodSpotsHistoryRepository>()
            val foodSpotsPhotoRepository = mockk<FoodSpotsPhotoRepository>()
            val foodSpotsRepository = mockk<FoodSpotsRepository>()
            val userRepository = mockk<UserRepository>()
            val reportFoodCategoryRepository = mockk<ReportFoodCategoryRepository>()
            val reportOperationHoursRepository = mockk<ReportOperationHoursRepository>()
            val reviewRepository = mockk<FoodSpotsReviewRepository>()
            val reviewPhotoRepository = mockk<FoodSpotsReviewPhotoRepository>()
            val imageService = spyk(ImageService(mockk()))
            val executor = mockk<ExecutorService>()
            val foodSPotsQueryService =
                FoodSpotsQueryService(
                    foodSpotsHistoryRepository,
                    foodSpotsPhotoRepository,
                    reportFoodCategoryRepository,
                    imageService,
                    foodSpotsRepository,
                    reportOperationHoursRepository,
                    reviewRepository,
                    userRepository,
                    reviewPhotoRepository,
                    executor,
                )
            afterEach { clearAllMocks() }

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

                `when`("정상적으로 카테고리 별 500m 거리 이내 음식점 검색이 가능한 경우") {
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

            given("getReportHistory 테스트") {
                every { foodSpotsHistoryRepository.getByHistoryId(any()) } returns createMockTestFoodHistory()
                every { foodSpotsPhotoRepository.getByHistoryId(any()) } returns
                    listOf(
                        createTestFoodSpotsPhoto(),
                    )
                every { imageService.getDownloadUrl(any()) } returns TEST_FOOD_SPOTS_PHOTO_URL
                every { reportFoodCategoryRepository.getByHistoryId(any()) } returns
                    listOf(
                        createTestReportFoodCategory(),
                    )
                every { reportOperationHoursRepository.getByHistoryId(any()) } returns
                    listOf(
                        createTestReportOperationHours(),
                    )
                every { executor.execute(any()) } answers {
                    firstArg<Runnable>().run()
                }
                `when`("정상적인 데이터가 들어올 경우") {
                    then("해당 리포트 이력 상세가 조회되어야 한다.") {
                        foodSPotsQueryService.getReportHistory(TEST_FOOD_SPOTS_HISTORY_ID)
                        verify(exactly = 1) {
                            foodSpotsHistoryRepository.getByHistoryId(any())
                            foodSpotsPhotoRepository.getByHistoryId(any())
                            imageService.getDownloadUrl(any())
                            reportFoodCategoryRepository.getByHistoryId(any())
                            reportOperationHoursRepository.getByHistoryId(any())
                        }
                    }
                }

                `when`("해당 이력이 존재하지 않는 경우") {
                    every { foodSpotsHistoryRepository.getByHistoryId(any()) } throws FoodSpotsHistoryNotFoundException()
                    then("FoodSpotsHistoryNotFoundException 이 발생한다.") {
                        shouldThrow<FoodSpotsHistoryNotFoundException> {
                            foodSPotsQueryService.getReportHistory(TEST_FOOD_SPOTS_HISTORY_ID)
                        }
                    }
                }
            }

            given("getFoodSpotsReview 테스트") {
                every { userRepository.findById(any()) } returns Optional.of(createTestUser())
                every {
                    reviewRepository.sliceByFoodSpots(
                        any(),
                        any(),
                        any(),
                        any(),
                    )
                } returns createMockSliceReview()
                every { imageService.getDownloadUrl(any()) } returns TEST_REVIEW_PHOTO_URL
                every { reviewPhotoRepository.getByReview(any()) } returns
                    listOf(
                        createTestReviewPhoto(),
                    )
                every { executor.execute(any()) } answers {
                    firstArg<Runnable>().run()
                }
                `when`("정상적인 데이터가 들어올 경우") {
                    then("정상적으로 리뷰가 조회되어야 한다.") {
                        foodSPotsQueryService.getFoodSpotsReview(
                            TEST_FOOD_SPOT_ID,
                            TEST_PAGE_SIZE,
                            TEST_LAST_ID,
                            ReviewSortType.LATEST,
                        )
                        verify(exactly = 1) {
                            userRepository.findById(any())
                            reviewRepository.sliceByFoodSpots(any(), any(), any(), any())
                            userRepository.findById(any())
                            reviewPhotoRepository.getByReview(any())
                        }
                        verify(exactly = 2) { imageService.getDownloadUrl(any()) }
                    }
                }
            }

            given("getFoodSpotsDetail 테스트") {
                every { foodSpotsRepository.getByFoodSpotsId(any()) } returns MockTestFoodSpot()
                every { foodSpotsHistoryRepository.findByFoodSpots(any()) } returns
                    listOf(
                        createTestFoodHistory(),
                    )
                every { foodSpotsPhotoRepository.findByHistoryIn(any()) } returns
                    listOf(createTestFoodSpotsPhoto())
                every { imageService.getDownloadUrl(any()) } returns TEST_FOOD_SPOTS_PHOTO_URL
                every { reviewRepository.getReviewAggregatedInfo(any()) } returns createTestAggregatedInfoResponse()
                every { executor.execute(any()) } answers {
                    firstArg<Runnable>().run()
                }
                `when`("정상적인 데이터가 들어올 경우") {
                    then("정상적으로 음식점 상세가 조회되어야 한다.") {
                        foodSPotsQueryService.getFoodSpotsDetail(TEST_FOOD_SPOT_ID)
                        verify(exactly = 1) {
                            foodSpotsRepository.getByFoodSpotsId(any())
                            foodSpotsHistoryRepository.findByFoodSpots(any())
                            foodSpotsPhotoRepository.findByHistoryIn(any())
                            imageService.getDownloadUrl(any())
                        }
                    }
                }

                `when`("해당 음식점이 존재하지 않는 경우") {
                    every { foodSpotsRepository.findById(any()) } returns Optional.empty()
                    then("FoodSpotsNotFoundException 이 발생한다.") {
                        shouldThrow<FoodSpotsNotFoundException> {
                            foodSPotsQueryService.getFoodSpotsDetail(TEST_FOOD_SPOT_ID)
                        }
                    }
                }
            }
        },
    )
