package kr.weit.roadyfoody.search.foodSpots.application.service

import jakarta.transaction.Transactional
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsQueryService
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponses
import kr.weit.roadyfoody.user.application.service.UserCommandService
import kr.weit.roadyfoody.user.domain.User
import org.springframework.stereotype.Service
import kotlin.math.pow

@Service
class FoodSpotsSearchService(
    private val foodSpotsQueryService: FoodSpotsQueryService,
    private val userCommandService: UserCommandService,
) {
    @Transactional
    fun searchFoodSpots(
        user: User,
        foodSpotsSearchQuery: FoodSpotsSearchCondition,
    ): FoodSpotsSearchResponses {
        val baseRadius = 500
        val searchRadius = foodSpotsSearchQuery.radius

        val additionalRadius = (searchRadius - baseRadius) / baseRadius
        val coinRequired = (2.0.pow(additionalRadius.toDouble()) * 100).toInt()
        if (coinRequired > user.coin) {
            throw RoadyFoodyBadRequestException(ErrorCode.COIN_NOT_ENOUGH)
        }
        val foodSpotsSearchResponses = foodSpotsQueryService.searchFoodSpots(foodSpotsSearchQuery)
        userCommandService.decreaseCoin(user.id, coinRequired)

        return foodSpotsSearchResponses
    }
}
