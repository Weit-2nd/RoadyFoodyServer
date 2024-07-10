package kr.weit.roadyfoody.search.foodSpots.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.foodSpots.validator.Latitude
import kr.weit.roadyfoody.foodSpots.validator.Longitude
import org.springdoc.core.annotations.ParameterObject

@ParameterObject
data class FoodSpotsSearchCondition(
    @field:NotNull(message = "경도는 필수입니다.")
    @field:Longitude
    val centerLongitude: Double,
    @field:NotNull(message = "위도는 필수입니다.")
    @field:Latitude
    val centerLatitude: Double,
    @field:NotNull(message = "반경은 필수입니다.")
    @field:Min(value = 500, message = "반경은 500 이상이어야 합니다.")
    val radius: Int,
    @Size(min = 1, max = 60, message = "검색어는 1자 이상 60자 이하로 입력해주세요.")
    val name: String?,
    val categoryIds: List<Long>? = emptyList(),
)
