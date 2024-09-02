package kr.weit.roadyfoody.review.application.service

import TEST_REVIEW_ID
import createMockReviewLike
import createMockTestReview
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManager
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.review.exception.FoodSpotsReviewNotFoundException
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.ReviewLikeRepository
import kr.weit.roadyfoody.review.repository.getReviewByReviewId
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import java.util.Optional

class ReviewLikeCommandServiceTest :
    BehaviorSpec(
        {
            val reviewRepository = mockk<FoodSpotsReviewRepository>()
            val reviewLikeRepository = mockk<ReviewLikeRepository>()
            val entityManager = mockk<EntityManager>()
            val reviewLikeService =
                ReviewLikeCommandService(
                    reviewRepository,
                    reviewLikeRepository,
                    entityManager,
                )
            val review = createMockTestReview()
            afterEach { clearAllMocks() }

            given("likeReview 테스트") {
                val expectedLikeTotal = review.likeTotal + 1
                every { reviewRepository.getReviewByReviewId(any()) } returns review
                every { reviewLikeRepository.existsById(any()) } returns false
                every { entityManager.merge(any<User>()) } returns createTestUser()
                every { reviewLikeRepository.save(any()) } returns createMockReviewLike()
                `when`("리뷰 좋아요 생성 요청시") {
                    then("리뷰 좋아요 이력 생성되고 리뷰의 좋아요 수가 증가한다.") {
                        reviewLikeService.likeReview(review.id, createTestUser())
                        verify(exactly = 1) {
                            reviewRepository.getReviewByReviewId(any())
                            reviewLikeRepository.existsById(any())
                            reviewLikeRepository.save(any())
                        }
                        review.likeTotal shouldBe expectedLikeTotal
                    }
                }

                `when`("리뷰가 존재하지 않는 경우") {
                    every { reviewRepository.findById(any()) } returns Optional.empty()
                    then("리뷰가 존재하지 않는다는 예외가 발생") {
                        shouldThrow<FoodSpotsReviewNotFoundException> {
                            reviewLikeService.likeReview(TEST_REVIEW_ID, createTestUser())
                        }
                    }
                }

                `when`("이미 좋아요를 누른 경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns createMockTestReview()
                    every { reviewLikeRepository.existsById(any()) } returns true
                    then("이미 좋아요를 눌렀다는 예외가 발생한다.") {
                        val ex =
                            shouldThrow<RoadyFoodyBadRequestException> {
                                reviewLikeService.likeReview(TEST_REVIEW_ID, createTestUser())
                            }
                        ex.message shouldBe ErrorCode.ALREADY_LIKED.errorMessage
                    }
                }
            }

            given("unlikeReview 테스트") {
                val expectedLikeTotal = review.likeTotal - 1
                every { reviewRepository.getReviewByReviewId(any()) } returns review
                every { reviewLikeRepository.existsById(any()) } returns true
                every { reviewLikeRepository.deleteById(any()) } returns Unit
                `when`("리뷰 좋아요 삭제 요청시") {
                    then("리뷰 좋아요 이력이 삭제되고 리뷰의 좋아요 수가 감소한다.") {
                        reviewLikeService.unlikeReview(TEST_REVIEW_ID, createTestUser())
                        verify(exactly = 1) {
                            reviewRepository.getReviewByReviewId(any())
                            reviewLikeRepository.existsById(any())
                            reviewLikeRepository.deleteById(any())
                        }
                        review.likeTotal shouldBe expectedLikeTotal
                    }
                }

                `when`("리뷰가 존재하지 않는 경우") {
                    every { reviewRepository.findById(any()) } returns Optional.empty()
                    then("리뷰가 존재하지 않는다는 예외가 발생") {
                        shouldThrow<FoodSpotsReviewNotFoundException> {
                            reviewLikeService.unlikeReview(TEST_REVIEW_ID, createTestUser())
                        }
                    }
                }

                `when`("좋아요를 누르지 않은 경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns createMockTestReview()
                    every { reviewLikeRepository.existsById(any()) } returns false
                    then("좋아요를 누르지 않은 리뷰라는 예외가 발생한다.") {
                        shouldThrow<RoadyFoodyBadRequestException> {
                            reviewLikeService.unlikeReview(TEST_REVIEW_ID, createTestUser())
                        }.message shouldBe ErrorCode.NOT_LIKED.errorMessage
                    }
                }

                `when`("좋아요 수가 0인 경우") {
                    every { reviewRepository.getReviewByReviewId(any()) } returns
                        createMockTestReview(
                            likeTotal = 0,
                        )
                    every { reviewLikeRepository.existsById(any()) } returns true
                    then("리뷰 좋아요 수는 음수가 될 수 없다는 예외가 발생한다.") {
                        shouldThrow<RoadyFoodyBadRequestException> {
                            reviewLikeService.unlikeReview(TEST_REVIEW_ID, createTestUser())
                        }.message shouldBe ErrorCode.NEGATIVE_NUMBER_OF_LIKED.errorMessage
                    }
                }
            }
        },
    )
