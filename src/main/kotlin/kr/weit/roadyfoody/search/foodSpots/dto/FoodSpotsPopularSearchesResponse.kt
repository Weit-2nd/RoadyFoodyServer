package kr.weit.roadyfoody.search.foodSpots.dto

import io.swagger.v3.oas.annotations.media.Schema

data class FoodSpotsPopularSearchesResponse(
    @Schema(description = "인기 검색어 순위", example = "1")
    val ranking: Int,
    @Schema(description = "검색어", example = "횟집")
    val keyword: String,
) {
    companion object {
        fun of(
            ranking: Int,
            keyword: String,
        ): FoodSpotsPopularSearchesResponse = FoodSpotsPopularSearchesResponse(ranking, keyword)
    }
}
