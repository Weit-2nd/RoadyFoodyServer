package kr.weit.roadyfoody.review.application.service

import USER_ENTITY_LOCK_KEY
import kr.weit.roadyfoody.badge.service.BadgeCommandService
import kr.weit.roadyfoody.global.annotation.DistributedLock
import kr.weit.roadyfoody.review.domain.FoodSpotsReviewFlag
import kr.weit.roadyfoody.review.exception.ReviewFlagAlreadyExistsException
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.ReviewFlagRepository
import kr.weit.roadyfoody.review.repository.getByIdWithPessimisticLock
import kr.weit.roadyfoody.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewFlagCommandService(
    private val reviewRepository: FoodSpotsReviewRepository,
    private val reviewFlagRepository: ReviewFlagRepository,
    private val reviewCommandService: ReviewCommandService,
    private val badgeCommandService: BadgeCommandService,
) {
    companion object {
        private const val FLAG_THRESHOLD = 4
    }

    @DistributedLock(lockName = USER_ENTITY_LOCK_KEY, identifier = "user")
    @Transactional
    fun flagReview(
        user: User,
        reviewId: Long,
    ) {
        val review = reviewRepository.getByIdWithPessimisticLock(reviewId)

        if (reviewFlagRepository.existsByReviewIdAndUserId(review.id, user.id)) {
            throw ReviewFlagAlreadyExistsException()
        }

        if (reviewFlagRepository.countByReviewId(review.id) >= FLAG_THRESHOLD) {
            reviewCommandService.deleteReviewCascade(review.id)
            badgeCommandService.tryChangeBadgeAndIfPromotedGiveBonus(review.user.id)
        } else {
            reviewFlagRepository.save(FoodSpotsReviewFlag(review = review, user = user))
        }
    }
}
