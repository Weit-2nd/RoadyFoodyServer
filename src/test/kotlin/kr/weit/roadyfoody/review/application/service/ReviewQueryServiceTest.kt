package kr.weit.roadyfoody.review.application.service

import TEST_REVIEW_ID
import TEST_REVIEW_PHOTO_URL
import createMockTestReview
import createTestReviewPhoto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.exception.FoodSpotsReviewNotFoundException
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.getByReview
import kr.weit.roadyfoody.user.repository.UserRepository
import java.util.Optional

class ReviewQueryServiceTest :
    BehaviorSpec(
        {
            val reviewRepository = mockk<FoodSpotsReviewRepository>()
            val reviewPhotoRepository = mockk<FoodSpotsReviewPhotoRepository>()
            val imageService = spyk(ImageService(mockk()))
            val userRepository = mockk<UserRepository>()
            val reportQueryService =
                ReviewQueryService(
                    userRepository,
                    reviewRepository,
                    reviewPhotoRepository,
                    imageService,
                )
            afterEach { clearAllMocks() }

            given("getReviewDetail 테스트") {
                `when`("해당 리뷰가 존재하는 경우") {
                    val review = createMockTestReview()
                    every { reviewRepository.findById(any()) } returns Optional.of(review)
                    every { reviewPhotoRepository.getByReview(any()) } returns
                        listOf(
                            createTestReviewPhoto(),
                        )
                    every { imageService.getDownloadUrl(any()) } returns TEST_REVIEW_PHOTO_URL
                    every { userRepository.findById(any()) } returns Optional.of(review.user)
                    then("해당 리뷰의 상세정보를 반환한다.")
                    reportQueryService.getReviewDetail(TEST_REVIEW_ID)
                    verify(exactly = 1) {
                        reviewRepository.findById(any())
                        reviewPhotoRepository.getByReview(any())
                        userRepository.findById(any())
                    }
                    verify(exactly = 2) {
                        imageService.getDownloadUrl(any())
                    }
                }

                `when`("리뷰가 존재하지 않는 경우") {
                    every { reviewRepository.findById(any()) } returns Optional.empty()
                    then("FoodSpotsReviewNotFoundException 예외 발생") {
                        shouldThrow<FoodSpotsReviewNotFoundException> {
                            reportQueryService.getReviewDetail(TEST_REVIEW_ID)
                        }
                    }
                }
            }
        },
    )
