package kr.weit.roadyfoody.review.repository

import jakarta.persistence.LockModeType
import kr.weit.roadyfoody.review.domain.FoodSpotsReviewFlag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock

interface ReviewFlagRepository : JpaRepository<FoodSpotsReviewFlag, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByReviewId(reviewId: Long): List<FoodSpotsReviewFlag>

    fun countByReviewId(reviewId: Long): Int

    fun deleteByReviewId(reviewId: Long)
}
