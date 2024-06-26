package kr.weit.roadyfoody.foodSpots.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.utils.FOOD_SPOTS_NAME_REGEX_DESC
import kr.weit.roadyfoody.foodSpots.utils.FOOD_SPOTS_NAME_REGEX_STR
import kr.weit.roadyfoody.foodSpots.validator.Latitude
import kr.weit.roadyfoody.foodSpots.validator.Longitude
import kr.weit.roadyfoody.global.utils.CoordinateUtils.Companion.createCoordinate
import kr.weit.roadyfoody.user.domain.User

data class ReportRequest(
    @field:Pattern(regexp = FOOD_SPOTS_NAME_REGEX_STR, message = FOOD_SPOTS_NAME_REGEX_DESC)
    @field:NotBlank(message = "상호명은 필수입니다.")
    val name: String,
    @field:NotNull(message = "경도는 필수입니다.")
    @field:Longitude
    val longitude: Double,
    @field:NotNull(message = "위도는 필수입니다.")
    @field:Latitude
    val latitude: Double,
    @NotNull(message = "음식점 여부는 필수입니다.")
    val foodTruck: Boolean,
    @NotNull(message = "영업 여부는 필수입니다.")
    val open: Boolean,
    @NotNull(message = "폐업 여부는 필수입니다.")
    val closed: Boolean,
) {
    fun toFoodSpotsEntity() =
        FoodSpots(
            name = name,
            point = createCoordinate(longitude, latitude),
            foodTruck = foodTruck,
            open = open,
            storeClosure = closed,
        )

    fun toFoodSpotsHistoryEntity(
        foodSpots: FoodSpots,
        user: User,
    ) = FoodSpotsHistory(
        name = name,
        foodSpots = foodSpots,
        user = user,
        point = createCoordinate(longitude, latitude),
        foodTruck = foodTruck,
        open = open,
        storeClosure = closed,
    )
}
