package kr.weit.roadyfoody.foodSpots.application.service

import TEST_FOOD_SPOT_ID
import TEST_REVIEW_PHOTO_URL
import createMockSliceReview
import createTestFoodSpotsReview
import createTestFoodSpotsReviews
import createTestReviewPhoto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kr.weit.roadyfoody.foodSpots.domain.DayOfWeek
import kr.weit.roadyfoody.foodSpots.exception.FoodSpotsHistoryNotFoundException
import kr.weit.roadyfoody.foodSpots.fixture.MockTestFoodSpot
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_HISTORY_ID
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_PHOTO_URL
import kr.weit.roadyfoody.foodSpots.fixture.TEST_OPERATION_HOURS_CLOSE
import kr.weit.roadyfoody.foodSpots.fixture.TEST_OPERATION_HOURS_OPEN
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodSpot
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodSpotList
import kr.weit.roadyfoody.foodSpots.fixture.createTestAggregatedInfoResponse
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodOperationHours
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
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import java.time.LocalDate
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
                    every { foodSpotsPhotoRepository.findOneByFoodSpots(any()) } returns null
                    every { reviewRepository.findByFoodSpots(any()) } returns createTestFoodSpotsReviews()
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
                    every { foodSpotsPhotoRepository.findOneByFoodSpots(any()) } returns null
                    every { reviewRepository.findByFoodSpots(any()) } returns createTestFoodSpotsReviews()
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
                    every { foodSpotsPhotoRepository.findOneByFoodSpots(any()) } returns null
                    every { reviewRepository.findByFoodSpots(any()) } returns createTestFoodSpotsReviews()
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

                `when`("리뷰가 존재할 경우") {
                    every {
                        foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                            0.0,
                            0.0,
                            500,
                            null,
                            emptyList(),
                        )
                    } returns createMockTestFoodSpotList()
                    every { foodSpotsPhotoRepository.findOneByFoodSpots(any()) } returns null
                    every { reviewRepository.findByFoodSpots(any()) } returns
                        listOf(
                            createTestFoodSpotsReview(rate = 4),
                            createTestFoodSpotsReview(rate = 3),
                            createTestFoodSpotsReview(rate = 3),
                        )
                    then("리뷰 개수와 평균 평점을 반환한다.") {
                        val foodSpotsSearchResponses =
                            foodSPotsQueryService.searchFoodSpots(query500m)
                        foodSpotsSearchResponses.items.first().run {
                            reviewCount shouldBe 3
                            averageRating shouldBe 3.3
                        }
                    }
                }

                `when`("리뷰가 존재하지 않을 경우") {
                    every {
                        foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                            0.0,
                            0.0,
                            500,
                            null,
                            emptyList(),
                        )
                    } returns createMockTestFoodSpotList()
                    every { foodSpotsPhotoRepository.findOneByFoodSpots(any()) } returns null
                    every { reviewRepository.findByFoodSpots(any()) } returns emptyList()
                    then("리뷰 개수 0 과 평균 평점 0.0 을 반환한다.") {
                        val foodSpotsSearchResponses =
                            foodSPotsQueryService.searchFoodSpots(query500m)
                        foodSpotsSearchResponses.items.first().run {
                            reviewCount shouldBe 0
                            averageRating shouldBe 0.0
                        }
                    }
                }

                `when`("금일 영업중인 음식점이 존재할 경우") {
                    every {
                        foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                            0.0,
                            0.0,
                            500,
                            null,
                            emptyList(),
                        )
                    } returns
                        listOf(
                            createMockTestFoodSpot(
                                operationHours =
                                    mutableListOf(
                                        createTestFoodOperationHours(
                                            dayOfWeek = DayOfWeek.MON,
                                            openingHours = TEST_OPERATION_HOURS_OPEN,
                                            closingHours = TEST_OPERATION_HOURS_CLOSE,
                                        ),
                                    ),
                            ),
                        )
                    every { foodSpotsPhotoRepository.findOneByFoodSpots(any()) } returns null
                    every { reviewRepository.findByFoodSpots(any()) } returns createTestFoodSpotsReviews()
                    val mockToday = mockk<LocalDate>()
                    every { mockToday.get(any()) } returns 1
                    then("금일 영업시간을 반환한다.") {
                        val foodSpotsSearchResponses =
                            foodSPotsQueryService.searchFoodSpots(
                                query500m,
                                today = mockToday,
                            )
                        foodSpotsSearchResponses.items.first().operationHours.run {
                            dayOfWeek shouldBe DayOfWeek.MON
                            openingHours shouldBe TEST_OPERATION_HOURS_OPEN
                            closingHours shouldBe TEST_OPERATION_HOURS_CLOSE
                        }
                    }
                }

                `when`("금일 영업중인 음식점이 존재하지 않을 경우") {
                    every {
                        foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                            0.0,
                            0.0,
                            500,
                            null,
                            emptyList(),
                        )
                    } returns
                        listOf(
                            createMockTestFoodSpot(
                                operationHours =
                                    mutableListOf(
                                        createTestFoodOperationHours(
                                            dayOfWeek = DayOfWeek.MON,
                                            openingHours = TEST_OPERATION_HOURS_CLOSE,
                                            closingHours = TEST_OPERATION_HOURS_OPEN,
                                        ),
                                    ),
                            ),
                        )
                    every { foodSpotsPhotoRepository.findOneByFoodSpots(any()) } returns null
                    every { reviewRepository.findByFoodSpots(any()) } returns createTestFoodSpotsReviews()
                    val mockOtherDay = mockk<LocalDate>()
                    every { mockOtherDay.get(any()) } returns 2
                    then("시작, 종료 시간이 00:00 인 응답을 반환한다.") {
                        val foodSpotsSearchResponses =
                            foodSPotsQueryService.searchFoodSpots(
                                query500m,
                                today = mockOtherDay,
                            )
                        foodSpotsSearchResponses.items.first().operationHours.run {
                            dayOfWeek shouldBe DayOfWeek.TUE
                            openingHours shouldBe "00:00"
                            closingHours shouldBe "00:00"
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
                every { foodSpotsPhotoRepository.findByHistoryId(any()) } returns
                    listOf(
                        createTestFoodSpotsPhoto(),
                    )
                every { imageService.getDownloadUrl(any()) } returns TEST_FOOD_SPOTS_PHOTO_URL
                every { reportFoodCategoryRepository.findByFoodSpotsHistoryId(any()) } returns
                    listOf(
                        createTestReportFoodCategory(),
                    )
                every { reportOperationHoursRepository.findByFoodSpotsHistoryId(any()) } returns
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
                            foodSpotsPhotoRepository.findByHistoryId(any())
                            imageService.getDownloadUrl(any())
                            reportFoodCategoryRepository.findByFoodSpotsHistoryId(any())
                            reportOperationHoursRepository.findByFoodSpotsHistoryId(any())
                            executor.execute(any())
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
                every { reviewPhotoRepository.findByFoodSpotsReview(any()) } returns
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
                            reviewPhotoRepository.findByFoodSpotsReview(any())
                            executor.execute(any())
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
                            executor.execute(any())
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
