package kr.weit.roadyfoody.search.foodSpots.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import kr.weit.roadyfoody.foodSpots.validator.Latitude
import kr.weit.roadyfoody.foodSpots.validator.Longitude
import kr.weit.roadyfoody.global.utils.FilteringConverter

@Schema(name = "FoodSpotsSearchCondition", description = "가게 조회 조건")
data class FoodSpotsSearchCondition(
    @field:NotNull(message = "경도는 필수입니다.")
    @field:Longitude
    @Schema(name = "centerLongitude", description = "검색 중심 경도", required = true, example = "127.074667")
    val centerLongitude: Double,
    @field:NotNull(message = "위도는 필수입니다.")
    @field:Latitude
    @Schema(name = "centerLatitude", description = "검색 중심 위도", required = true, example = "37.147030")
    val centerLatitude: Double,
    @field:NotNull(message = "반경은 필수입니다.")
    @field:Min(value = 500, message = "반경은 500 이상이어야 합니다.")
    @Schema(name = "radius", description = "검색 반경", required = true, example = "500")
    val radius: Int,
    @Schema(name = "name", description = "가게 이름", required = false, example = "pot2")
    val name: String?,
    @Schema(name = "categoryIds", description = "음식 카테고리 ID 리스트 - 값이 없으면 빈문자열을 보내주세요.", required = false, example = "1,2")
    val categoryIds: String?,
) {
    fun toQuery(): FoodSpotsSearchQuery {
        return FoodSpotsSearchQuery(
            centerLongitude = centerLongitude,
            centerLatitude = centerLatitude,
            radius = radius,
            name = name,
            categoryIds = convertToCategoryIds(),
        )
    }

    private fun convertToCategoryIds(): List<Long> {
        if (categoryIds.isNullOrBlank()) return listOf()
        return FilteringConverter.convertToCategoryIds(categoryIds!!)
    }
}

data class FoodSpotsSearchQuery(
    val centerLongitude: Double,
    val centerLatitude: Double,
    val radius: Int,
    val name: String?,
    val categoryIds: List<Long>?,
)
