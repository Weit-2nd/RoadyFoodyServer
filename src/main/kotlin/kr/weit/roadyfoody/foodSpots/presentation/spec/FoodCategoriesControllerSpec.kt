package kr.weit.roadyfoody.foodSpots.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kr.weit.roadyfoody.foodSpots.application.dto.FoodCategoryResponse

interface FoodCategoriesControllerSpec {
    @Operation(
        description = "음식점 카테고리 리스트 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "카테고리 리스트",
            ),
        ],
    )
    fun getCategories(): List<FoodCategoryResponse>
}
