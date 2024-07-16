package kr.weit.roadyfoody.review.service

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
import kr.weit.roadyfoody.review.repository.FoodSportsReviewRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.user.fixture.createTestUser
import java.util.Optional
import java.util.concurrent.ExecutorService

class ReviewServiceTest :
    BehaviorSpec(
        {
            val reviewRepository = mockk<FoodSportsReviewRepository>()
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
        },
    )
