package kr.weit.roadyfoody.review.application.service

import createMockSliceReview
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kr.weit.roadyfoody.global.TEST_LAST_ID
import kr.weit.roadyfoody.global.TEST_PAGE_SIZE
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.user.exception.UserNotFoundException
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestUser
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

            given("getUserReviews 테스트") {
                every { userRepository.findById(any()) } returns Optional.of(createTestUser())
                every {
                    reviewRepository.sliceByUser(
                        any(),
                        any(),
                        any(),
                    )
                } returns createMockSliceReview()
                `when`("정상적인 데이터가 들어올 경우") {
                    then("정상적으로 리뷰가 조회되어야 한다.") {
                        reportQueryService.getUserReviews(
                            TEST_USER_ID,
                            TEST_PAGE_SIZE,
                            TEST_LAST_ID,
                        )
                    }
                }

                `when`("유저가 없을 경우") {
                    every { userRepository.findById(any()) } returns Optional.empty()
                    then("UserNotFoundException 예외 발생") {
                        shouldThrow<UserNotFoundException> {
                            reportQueryService.getUserReviews(
                                TEST_USER_ID,
                                TEST_PAGE_SIZE,
                                TEST_LAST_ID,
                            )
                        }
                    }
                }
            }
        },
    )
