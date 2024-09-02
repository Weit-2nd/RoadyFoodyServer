package kr.weit.roadyfoody.review.application.service

import REVIEW_LIKE_LOCK_KEY
import jakarta.persistence.EntityManager
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.global.annotation.DistributedLock
import kr.weit.roadyfoody.review.domain.ReviewLike
import kr.weit.roadyfoody.review.domain.ReviewLikeId
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
    private val entityManager: EntityManager,
) {
    @Transactional
    @DistributedLock(lockName = REVIEW_LIKE_LOCK_KEY, identifier = "reviewId")
    fun likeReview(
        reviewId: Long,
        user: User,
    ) {
        val review = reviewRepository.getReviewByReviewId(reviewId)
        if (reviewLikeRepository.existsById(ReviewLikeId(review, user))) {
            throw RoadyFoodyBadRequestException(ErrorCode.ALREADY_LIKED)
        }
        review.increaseLike()
        reviewLikeRepository.save(ReviewLike(review, entityManager.merge(user)))
    }

    @Transactional
    @DistributedLock(lockName = REVIEW_LIKE_LOCK_KEY, identifier = "reviewId")
    fun unlikeReview(
        reviewId: Long,
        user: User,
    ) {
        val review = reviewRepository.getReviewByReviewId(reviewId)
        if (!reviewLikeRepository.existsById(ReviewLikeId(review, user))) {
            throw RoadyFoodyBadRequestException(ErrorCode.NOT_LIKED)
        }
        if (review.likeTotal <= 0) {
            throw RoadyFoodyBadRequestException(ErrorCode.NEGATIVE_NUMBER_OF_LIKED)
        }
        review.decreaseLike()
        reviewLikeRepository.deleteById(ReviewLikeId(review, user))
    }
}
