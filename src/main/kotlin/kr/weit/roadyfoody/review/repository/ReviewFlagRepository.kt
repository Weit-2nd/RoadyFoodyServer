package kr.weit.roadyfoody.review.repository

import kr.weit.roadyfoody.review.domain.FoodSpotsReviewFlag
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewFlagRepository : JpaRepository<FoodSpotsReviewFlag, Long> {
    fun countByReviewId(reviewId: Long): Int

    fun existsByReviewIdAndUserId(
        reviewId: Long,
        userId: Long,
    ): Boolean

    fun deleteByReviewId(reviewId: Long)
}
