package kr.weit.roadyfoody.review.application.service

import createMockTestReview
import createTestReviewPhoto
import createTestReviewRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kr.weit.roadyfoody.foodSpots.fixture.createMockPhotoList
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodSpot
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.getByFoodSpotsId
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.domain.FoodSpotsReviewPhoto
import kr.weit.roadyfoody.review.exception.FoodSpotsNotFoundException
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.user.fixture.createTestUser
import java.util.Optional
import java.util.concurrent.ExecutorService

class ReviewCommandServiceTest :
    BehaviorSpec(
        {
            val reviewRepository = mockk<FoodSpotsReviewRepository>()
            val reviewPhotoRepository = mockk<FoodSpotsReviewPhotoRepository>()
            val foodSpotsRepository = mockk<FoodSpotsRepository>()
            val imageService = spyk(ImageService(mockk()))
            val executor = mockk<ExecutorService>()
            val reportService =
                ReviewCommandService(
                    reviewRepository,
                    reviewPhotoRepository,
                    foodSpotsRepository,
                    imageService,
                    executor,
                )

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
                `when`("정상적인 데이터와 이미지가 들어올 경우") {
                    then("정상적으로 저장되어야 한다.") {
                        reportService.createReview(
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
                            reportService.createReview(
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
                        reportService.deleteWithdrewUserReview(createTestUser())
                    }
                }
            }
        },
    )
