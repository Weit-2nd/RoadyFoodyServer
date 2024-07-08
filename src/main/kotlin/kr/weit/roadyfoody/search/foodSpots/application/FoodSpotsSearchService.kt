package kr.weit.roadyfoody.search.foodSpots.application

import kr.weit.roadyfoody.foodSpots.domain.DayOfWeek
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponse
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponses
import kr.weit.roadyfoody.search.foodSpots.dto.OperationStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

@Service
class FoodSpotsSearchService(
    private val foodSpotsRepository: FoodSpotsRepository,
) {
    @Transactional
    fun searchFoodSpots(
        centerLongitude: Double,
        centerLatitude: Double,
        radius: Int,
        name: String?,
        categoryIds: List<Long>?,
    ): FoodSpotsSearchResponses {
        val result: List<FoodSpots> =
            foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                centerLongitude,
                centerLatitude,
                radius,
                name,
                categoryIds ?: emptyList(),
            )

        val today = LocalDate.now()
        val dayOfWeekValue = today.get(ChronoField.DAY_OF_WEEK)
        val dayOfWeek: DayOfWeek = DayOfWeek.of(dayOfWeekValue)

        val now = LocalTime.now()
        val format: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val foodSpotsSearchResponses =
            result.map { it ->
                var openValue: OperationStatus = OperationStatus.OPEN
                if (it.open) {
                    it.foodSpotsOperationHours.map {
                        if (it.dayOfWeek == dayOfWeek) {
                            if (now.isAfter(
                                    LocalTime.parse(it.openingHours, format),
                                ) && now.isBefore(LocalTime.parse(it.closingHours, format))
                            ) {
                                openValue = OperationStatus.OPEN
                            } else {
                                openValue = OperationStatus.CLOSED
                            }
                        }
                    }
                } else {
                    OperationStatus.TEMPORARILY_CLOSED
                }
                FoodSpotsSearchResponse(
                    id = it.id,
                    name = it.name,
                    longitude = it.point.x,
                    latitude = it.point.y,
                    open = openValue,
                    foodCategories = it.foodCategories.map { it.foodCategory.name },
                )
            }

        return FoodSpotsSearchResponses(foodSpotsSearchResponses)
    }
}
