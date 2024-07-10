package kr.weit.roadyfoody.foodSpots.domain

import java.io.Serializable

data class ReportOperationHoursId(
    val foodSpotsHistory: FoodSpotsHistory,
    val dayOfWeek: DayOfWeek,
) : Serializable
