package kr.weit.roadyfoody.search.foodSpots.application.service

import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsQueryService
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponses
import kr.weit.roadyfoody.user.application.UserCommandService
import kr.weit.roadyfoody.user.domain.User
import org.springframework.stereotype.Service
import kotlin.math.pow

@Service
class FoodSpotsSearchService(
    private val foodSpotsQueryService: FoodSpotsQueryService,
    private val userCommandService: UserCommandService,
) {
    fun searchFoodSpots(
        user: User,
        foodSpotsSearchQuery: FoodSpotsSearchCondition,
    ): FoodSpotsSearchResponses {
        val baseRadius = 500
        val searchRadius = foodSpotsSearchQuery.radius

        val additionalRadius = (searchRadius - baseRadius) / baseRadius
        val coinRequired = (2.0.pow(additionalRadius.toDouble()) * 100).toInt()
        userCommandService.decreaseCoin(user.id, coinRequired)

        return foodSpotsQueryService.searchFoodSpots(foodSpotsSearchQuery)
    }
}
