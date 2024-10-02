package kr.weit.roadyfoody.review.application.service

import TEST_REVIEW_ID
import createMockTestReview
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.badge.service.BadgeCommandService
import kr.weit.roadyfoody.review.domain.FoodSpotsReviewFlag
import kr.weit.roadyfoody.review.exception.ReviewFlagAlreadyExistsException
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.ReviewFlagRepository
import kr.weit.roadyfoody.user.fixture.createTestUser

class ReviewFlagCommandServiceTest :
    BehaviorSpec({
        val reviewRepository = mockk<FoodSpotsReviewRepository>()
        val reviewFlagRepository = mockk<ReviewFlagRepository>()
        val reviewCommandService = mockk<ReviewCommandService>()
        val badgeCommandService = mockk<BadgeCommandService>()
        val reviewFlagCommandService =
            ReviewFlagCommandService(
                reviewRepository,
                reviewFlagRepository,
                reviewCommandService,
                badgeCommandService,
            )

        val user = createTestUser()

        afterEach { clearAllMocks() }

        given("flagReview 테스트") {
            `when`("정상적인 요청일 시 ") {
                every { reviewRepository.findReviewById(any()) } returns createMockTestReview()
                every { reviewFlagRepository.existsByReviewIdAndUserId(any(), any()) } returns false
                every { reviewFlagRepository.countByReviewId(any()) } returns 3
                every { reviewFlagRepository.save(any()) } returns
                    FoodSpotsReviewFlag(review = createMockTestReview(), user = createTestUser())

                then("리뷰신고 저장을 성공한다.") {
                    reviewFlagCommandService.flagReview(user, TEST_REVIEW_ID)

                    verify(exactly = 1) { reviewFlagRepository.save(any()) }
                }
            }

            `when`("이미 신고를 한 경우") {
                every { reviewRepository.findReviewById(any()) } returns createMockTestReview()
                every { reviewFlagRepository.existsByReviewIdAndUserId(any(), any()) } returns true

                then("예외가 발생한다.") {
                    shouldThrow<ReviewFlagAlreadyExistsException> {
                        reviewFlagCommandService.flagReview(user, TEST_REVIEW_ID)
                    }
                }
            }

            `when`("신고 임계값을 초과하는 경우") {
                every { reviewRepository.findReviewById(any()) } returns createMockTestReview()
                every { reviewFlagRepository.existsByReviewIdAndUserId(any(), any()) } returns false
                every { reviewFlagRepository.countByReviewId(any()) } returns 4
                every { reviewCommandService.deleteReviewCascade(any()) } returns Unit
                every { badgeCommandService.tryChangeBadgeAndIfPromotedGiveBonus(any()) } returns Unit

                then("해당 리뷰 제거에 성공한다.") {
                    reviewFlagCommandService.flagReview(user, TEST_REVIEW_ID)

                    verify(exactly = 1) {
                        reviewCommandService.deleteReviewCascade(any())
                        badgeCommandService.tryChangeBadgeAndIfPromotedGiveBonus(any())
                    }
                }
            }
        }
    })
