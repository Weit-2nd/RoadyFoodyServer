package kr.weit.roadyfoody.review.application.service

import TEST_REVIEW_ID
import createMockTestReview
import createTestReviewPhoto
import createTestReviewRequest
import createTestReviewUpdateRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import kr.weit.roadyfoody.badge.service.BadgeCommandService
import kr.weit.roadyfoody.foodSpots.fixture.createMockPhotoList
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodSpot
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.getByFoodSpotsId
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.domain.FoodSpotsReviewPhoto
import kr.weit.roadyfoody.review.exception.FoodSpotsNotFoundException
import kr.weit.roadyfoody.review.exception.FoodSpotsReviewNotFoundException
import kr.weit.roadyfoody.review.exception.NotFoodSpotsReviewOwnerException
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.ReviewLikeRepository
import kr.weit.roadyfoody.review.repository.getReviewByReviewId
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.user.fixture.TEST_OTHER_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestUser
import java.util.Optional
import java.util.concurrent.ExecutorService

class ReviewCommandServiceTest :
    BehaviorSpec(
        {
            val reviewRepository = mockk<FoodSpotsReviewRepository>()
            val reviewPhotoRepository = mockk<FoodSpotsReviewPhotoRepository>()
            val foodSpotsRepository = mockk<FoodSpotsRepository>()
            val reviewLikeRepository = mockk<ReviewLikeRepository>()
            val imageService = spyk(ImageService(mockk()))
            val executor = mockk<ExecutorService>()
            val badgeCommandService = mockk<BadgeCommandService>()
            val reviewService =
                ReviewCommandService(
                    reviewRepository,
                    reviewPhotoRepository,
                    foodSpotsRepository,
                    reviewLikeRepository,
                    imageService,
                    executor,
                    badgeCommandService,
                )
            afterEach { clearAllMocks() }

            given("createReview 테스트") {
                every { foodSpotsRepository.getByFoodSpotsId(any()) } returns createMockTestFoodSpot()
                every { reviewRepository.save(any()) } returns createMockTestReview()
                every { reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>()) } returns
                    mutableListOf(
                        createTestReviewPhoto(),
                    )
                every { imageService.upload(any(), any()) } returns Unit
                every { executor.execute(any()) } answers {
                    firstArg<Runnable>().run()
                }
                every { badgeCommandService.tryChangeBadgeAndIfPromotedGiveBonus(any()) } just runs
                `when`("정상적인 데이터와 이미지가 들어올 경우") {
                    then("정상적으로 저장되어야 한다.") {
                        reviewService.createReview(
                            createTestUser(),
                            createTestReviewRequest(),
                            createMockPhotoList(ImageFormat.WEBP),
                        )
                    }
                }

                `when`("음식점이 존재하지 않는 경우") {
                    every { foodSpotsRepository.findById(any()) } returns Optional.empty()
                    then("FoodSpotsNotFoundException 이 발생해야 한다.") {
                        shouldThrow<FoodSpotsNotFoundException> {
                            reviewService.createReview(
                                createTestUser(),
                                createTestReviewRequest(),
                                createMockPhotoList(ImageFormat.WEBP),
                            )
                        }
                    }
                }
            }

            given("deleteWithdrewUserReview 테스트") {
                every { reviewRepository.findByUser(any()) } returns listOf(createMockTestReview())
                every { reviewPhotoRepository.findByFoodSpotsReviewIn(any()) } returns
                    listOf(
                        createTestReviewPhoto(),
                    )
                every { imageService.remove(any()) } returns Unit
                every { reviewRepository.deleteAll(any()) } returns Unit
                every { reviewPhotoRepository.deleteAll(any()) } returns Unit
                `when`("정상적인 삭제 요청이 들어올 경우") {
                    then("정상적으로 삭제되어야 한다.") {
                        reviewService.deleteWithdrewUserReview(createTestUser())
                    }
                }
            }

            given("deleteReview 테스트") {
                `when`("리뷰의 주인이 아닌경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns
                        createMockTestReview(createTestUser(TEST_OTHER_USER_ID))
                    then("예외가 발생한다.") {
                        shouldThrow<NotFoodSpotsReviewOwnerException> {
                            reviewService.deleteReview(createTestUser(), TEST_REVIEW_ID)
                        }
                    }
                }
                `when`("리뷰 삭제 요청이 들어올 경우") {
                    val user = createTestUser()
                    every { reviewRepository.getReviewByReviewId(any()) } returns
                        createMockTestReview(
                            user,
                        )
                    every { reviewLikeRepository.deleteByReview(any()) } returns Unit
                    every { reviewRepository.deleteById(any()) } returns Unit
                    every { reviewPhotoRepository.findByFoodSpotsReviewIn(any()) } returns
                        listOf(
                            createTestReviewPhoto(),
                        )
                    every { imageService.remove(any()) } returns Unit
                    every { reviewPhotoRepository.deleteAll(any()) } returns Unit
                    every { reviewRepository.delete(any()) } returns Unit
                    every { badgeCommandService.tryChangeBadgeAndIfPromotedGiveBonus(any()) } just runs
                    then("정상적으로 삭제되어야 한다.") {
                        reviewService.deleteReview(user, TEST_REVIEW_ID)
                        verify(exactly = 1) {
                            imageService.remove(any())
                            reviewLikeRepository.deleteByReview(any())
                            reviewPhotoRepository.deleteAll(any())
                            reviewRepository.delete(any())
                        }
                    }
                }
            }

            given("updateReview 테스트") {
                val user = createTestUser()
                `when`("리뷰 수정 요청이 들어올 경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns
                        createMockTestReview(
                            user,
                        )
                    every {
                        reviewPhotoRepository.findByFoodSpotsReviewAndIdIn(
                            any(),
                            any(),
                        )
                    } returns
                        listOf(createTestReviewPhoto(), createTestReviewPhoto())
                    every { reviewPhotoRepository.findByFoodSpotsReview(any()) } returns emptyList()
                    every { reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>()) } returns
                        mutableListOf(createTestReviewPhoto())
                    every { reviewPhotoRepository.deleteAll(any()) } returns Unit
                    every { imageService.remove(any()) } returns Unit
                    every { imageService.upload(any(), any()) } returns Unit
                    every { executor.execute(any()) } answers {
                        firstArg<Runnable>().run()
                    }
                    then("정상적으로 수정되어야 한다.") {
                        reviewService.updateReview(
                            user,
                            TEST_REVIEW_ID,
                            createTestReviewUpdateRequest(deletePhotoIds = setOf(1L, 2L)),
                            createMockPhotoList(ImageFormat.WEBP, size = 2),
                        )
                        verify(exactly = 1) {
                            reviewRepository.getReviewByReviewId(any())
                            reviewPhotoRepository.findByFoodSpotsReviewAndIdIn(any(), any())
                            reviewPhotoRepository.findByFoodSpotsReview(any())
                            reviewPhotoRepository.deleteAll(any())
                            reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>())
                            reviewPhotoRepository.deleteAll(any())
                        }
                        verify(exactly = 2) {
                            imageService.remove(any())
                            imageService.upload(any(), any())
                        }
                    }
                }

                `when`("리뷰 내용만 수정할 경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns
                        createMockTestReview(
                            user,
                        )
                    every { reviewPhotoRepository.findByFoodSpotsReview(any()) } returns emptyList()
                    every { reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>()) } returns emptyList()
                    then("정상적으로 수정되어야 한다.") {
                        reviewService.updateReview(
                            user,
                            TEST_REVIEW_ID,
                            createTestReviewUpdateRequest(contents = "수정된 리뷰 내용", null, null),
                            null,
                        )
                        verify(exactly = 1) {
                            reviewRepository.getReviewByReviewId(any())
                            reviewPhotoRepository.findByFoodSpotsReview(any())
                            reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>())
                        }
                    }
                }

                `when`("리뷰 평점만 수정할 경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns
                        createMockTestReview(
                            user,
                        )
                    every { reviewPhotoRepository.findByFoodSpotsReview(any()) } returns emptyList()
                    every { reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>()) } returns emptyList()
                    then("정상적으로 수정되어야 한다.") {
                        reviewService.updateReview(
                            user,
                            TEST_REVIEW_ID,
                            createTestReviewUpdateRequest(null, 5, null),
                            null,
                        )
                        verify(exactly = 1) {
                            reviewRepository.getReviewByReviewId(any())
                            reviewPhotoRepository.findByFoodSpotsReview(any())
                            reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>())
                        }
                    }
                }

                `when`("리뷰 사진만 삭제할 경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns
                        createMockTestReview(
                            user,
                        )
                    every {
                        reviewPhotoRepository.findByFoodSpotsReviewAndIdIn(
                            any(),
                            any(),
                        )
                    } returns
                        listOf(createTestReviewPhoto(), createTestReviewPhoto())
                    every { reviewPhotoRepository.findByFoodSpotsReview(any()) } returns emptyList()
                    every { reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>()) } returns emptyList()
                    every { reviewPhotoRepository.deleteAll(any()) } returns Unit
                    every { imageService.remove(any()) } returns Unit
                    every { executor.execute(any()) } answers {
                        firstArg<Runnable>().run()
                    }
                    then("정상적으로 수정되어야 한다.") {
                        reviewService.updateReview(
                            user,
                            TEST_REVIEW_ID,
                            createTestReviewUpdateRequest(null, null, setOf(1L, 2L)),
                            null,
                        )
                        verify(exactly = 1) {
                            reviewRepository.getReviewByReviewId(any())
                            reviewPhotoRepository.findByFoodSpotsReview(any())
                            reviewPhotoRepository.findByFoodSpotsReviewAndIdIn(any(), any())
                            reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>())
                            reviewPhotoRepository.deleteAll(any())
                        }
                        verify(exactly = 2) {
                            imageService.remove(any())
                            executor.execute(any())
                        }
                    }
                }

                `when`("리뷰 사진만 추가할 경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns
                        createMockTestReview(
                            user,
                        )
                    every { reviewPhotoRepository.findByFoodSpotsReview(any()) } returns emptyList()
                    every { reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>()) } returns
                        mutableListOf(createTestReviewPhoto())
                    every { imageService.upload(any(), any()) } returns Unit
                    every { executor.execute(any()) } answers {
                        firstArg<Runnable>().run()
                    }
                    then("정상적으로 수정되어야 한다.") {
                        reviewService.updateReview(
                            user,
                            TEST_REVIEW_ID,
                            null,
                            createMockPhotoList(ImageFormat.WEBP),
                        )
                        verify(exactly = 1) {
                            reviewRepository.getReviewByReviewId(any())
                            reviewPhotoRepository.findByFoodSpotsReview(any())
                            reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>())
                        }
                        verify(exactly = 2) {
                            imageService.upload(any(), any())
                            executor.execute(any())
                        }
                    }
                }

                `when`("리뷰 사진 등록과 삭제가 동시에 되는 경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns
                        createMockTestReview(
                            user,
                        )
                    every {
                        reviewPhotoRepository.findByFoodSpotsReviewAndIdIn(
                            any(),
                            any(),
                        )
                    } returns
                        listOf(createTestReviewPhoto(), createTestReviewPhoto())
                    every { reviewPhotoRepository.findByFoodSpotsReview(any()) } returns
                        listOf(
                            createTestReviewPhoto(),
                        )
                    every { reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>()) } returns
                        mutableListOf(
                            createTestReviewPhoto(),
                            createTestReviewPhoto(),
                            createTestReviewPhoto(),
                        )
                    every { reviewPhotoRepository.deleteAll(any()) } returns Unit
                    every { imageService.remove(any()) } returns Unit
                    every { imageService.upload(any(), any()) } returns Unit
                    every { executor.execute(any()) } answers {
                        firstArg<Runnable>().run()
                    }
                    then("정상적으로 수정되어야 한다.") {
                        reviewService.updateReview(
                            user,
                            TEST_REVIEW_ID,
                            createTestReviewUpdateRequest(null, null, setOf(1L, 2L)),
                            createMockPhotoList(ImageFormat.WEBP),
                        )
                        verify(exactly = 1) {
                            reviewRepository.getReviewByReviewId(any())
                            reviewPhotoRepository.findByFoodSpotsReviewAndIdIn(any(), any())
                            reviewPhotoRepository.findByFoodSpotsReview(any())
                            reviewPhotoRepository.deleteAll(any())
                            reviewPhotoRepository.saveAll(any<List<FoodSpotsReviewPhoto>>())
                        }
                        verify(exactly = 2) {
                            imageService.remove(any())
                            imageService.upload(any(), any())
                        }
                    }
                }

                `when`("리뷰의 주인이 아닌경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns
                        createMockTestReview(
                            createTestUser(TEST_OTHER_USER_ID),
                        )
                    then("NotFoodSpotsReviewOwnerException 예외가 발생한다.") {
                        shouldThrow<NotFoodSpotsReviewOwnerException> {
                            reviewService.updateReview(createTestUser(), TEST_REVIEW_ID, null, null)
                        }
                    }
                }

                `when`("리뷰가 존재하지 않는 경우") {
                    every { reviewRepository.findById(any()) } returns Optional.empty()
                    then("FoodSpotsReviewNotFoundException 예외가 발생한다.") {
                        shouldThrow<FoodSpotsReviewNotFoundException> {
                            reviewService.updateReview(createTestUser(), TEST_REVIEW_ID, null, null)
                        }
                    }
                }

                `when`("리뷰의 사진이 3개 이상인 경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns
                        createMockTestReview(
                            user,
                        )
                    every { reviewPhotoRepository.findByFoodSpotsReview(any()) } returns
                        listOf(
                            createTestReviewPhoto(),
                            createTestReviewPhoto(),
                        )
                    then("IllegalArgumentException 예외가 발생한다.") {
                        shouldThrow<IllegalArgumentException> {
                            reviewService.updateReview(
                                user,
                                TEST_REVIEW_ID,
                                null,
                                createMockPhotoList(ImageFormat.WEBP, size = 2),
                            )
                        }
                    }
                }
            }
        },
    )
