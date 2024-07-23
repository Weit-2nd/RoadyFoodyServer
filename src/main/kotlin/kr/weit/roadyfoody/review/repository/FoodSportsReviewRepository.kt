package kr.weit.roadyfoody.review.repository

import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface FoodSportsReviewRepository : JpaRepository<FoodSpotsReview, Long> {
    fun findByUser(user: User): List<FoodSpotsReview>
}
