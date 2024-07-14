package kr.weit.roadyfoody.review.repository

import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import org.springframework.data.jpa.repository.JpaRepository

interface FoodSportsReviewRepository : JpaRepository<FoodSpotsReview, Long>
