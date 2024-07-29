package kr.weit.roadyfoody.foodSpots.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.foodSpots.domain.FoodCategory

data class FoodCategoryResponse(
    @Schema(description = "카테고리 id", example = "1L")
    val id: Long,
    @Schema(description = "카테고리명", example = "붕어빵")
    val name: String,
) {
    companion object {
        fun of(foodCategory: FoodCategory) = FoodCategoryResponse(foodCategory.id, foodCategory.name)
    }
}
