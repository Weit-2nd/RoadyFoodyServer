package kr.weit.roadyfoody.review.application.service

import REVIEW_LIKE_LOCK_KEY
import kr.weit.roadyfoody.global.annotation.DistributedLock
import kr.weit.roadyfoody.review.application.dto.ToggleLikeResponse
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.ReviewLike
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.ReviewLikeRepository
import kr.weit.roadyfoody.review.repository.getReviewByReviewId
import kr.weit.roadyfoody.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewLikeCommandService(
    private val reviewRepository: FoodSpotsReviewRepository,
    private val reviewLikeRepository: ReviewLikeRepository,
) {
    @Transactional
    @DistributedLock(lockName = REVIEW_LIKE_LOCK_KEY, identifier = "reviewId")
    fun toggleLike(
        reviewId: Long,
        user: User,
    ): ToggleLikeResponse {
        val review = reviewRepository.getReviewByReviewId(reviewId)
        var liked = true
        if (reviewLikeRepository.existsByReviewAndUser(review, user)) {
            review.decreaseLike()
            reviewLikeRepository.deleteByReviewAndUser(review, user)
            liked = false
        } else {
            review.increaseLike()
            reviewLikeRepository.save(ReviewLike(review, user))
        }

        return ToggleLikeResponse(reviewId, review.likeTotal, liked)
    }

    @Transactional
    @DistributedLock(lockName = REVIEW_LIKE_LOCK_KEY, identifier = "reviewId")
    fun decreaseLikeLock(
        review: FoodSpotsReview,
        reviewId: Long,
    ) {
        review.decreaseLike()
    }
}
