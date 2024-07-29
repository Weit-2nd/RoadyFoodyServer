package kr.weit.roadyfoody.review.repository

import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.exception.FoodSpotsReviewNotFoundException
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

fun FoodSpotsReviewRepository.getReviewByReviewId(reviewId: Long): FoodSpotsReview =
    findById(reviewId).orElseThrow {
        FoodSpotsReviewNotFoundException("해당 리뷰가 존재하지 않습니다.")
    }

interface FoodSpotsReviewRepository : JpaRepository<FoodSpotsReview, Long> {
    fun findByUser(user: User): List<FoodSpotsReview>
}
