package kr.weit.roadyfoody.foodSpots.domain

import java.io.Serializable

data class FoodSpotsOperationHoursId(
    val foodSpots: FoodSpots,
    val dayOfWeek: DayOfWeek,
) : Serializable {
    private constructor() : this(FoodSpots(), DayOfWeek.MON)
}
