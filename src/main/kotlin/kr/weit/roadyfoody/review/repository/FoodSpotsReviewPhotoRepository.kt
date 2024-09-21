package kr.weit.roadyfoody.review.repository

import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.FoodSpotsReviewPhoto
import org.springframework.data.jpa.repository.JpaRepository

fun FoodSpotsReviewPhotoRepository.getByReview(foodSpotsReview: FoodSpotsReview): List<FoodSpotsReviewPhoto> =
    findByFoodSpotsReview(foodSpotsReview)

interface FoodSpotsReviewPhotoRepository : JpaRepository<FoodSpotsReviewPhoto, Long> {
    fun findByFoodSpotsReviewIn(foodSpotsReviewList: List<FoodSpotsReview>): List<FoodSpotsReviewPhoto>

    fun findByFoodSpotsReview(foodSpotsReview: FoodSpotsReview): List<FoodSpotsReviewPhoto>

    fun findByFoodSpotsReviewAndIdIn(
        foodSpotsReview: FoodSpotsReview,
        ids: Set<Long>,
    ): List<FoodSpotsReviewPhoto>
}
