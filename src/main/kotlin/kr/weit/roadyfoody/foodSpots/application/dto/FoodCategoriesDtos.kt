package kr.weit.roadyfoody.foodSpots.application.dto

import kr.weit.roadyfoody.foodSpots.domain.FoodCategory

data class FoodCategoryResponse(
    val id: Long,
    val name: String,
) {
    companion object {
        fun of(foodCategory: FoodCategory) = FoodCategoryResponse(foodCategory.id, foodCategory.name)
    }
}
