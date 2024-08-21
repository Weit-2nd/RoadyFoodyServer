package kr.weit.roadyfoody.search.foodSpots.presentation.api

import kr.weit.roadyfoody.search.foodSpots.application.service.FoodSpotsSearchService
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.dto.RequiredCoinRequest
import kr.weit.roadyfoody.search.foodSpots.presentation.spec.FoodSpotsSearchControllerSpec
import kr.weit.roadyfoody.user.domain.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/food-spots")
class FoodSpotsSearchController(
    private val foodSpotsSearchService: FoodSpotsSearchService,
) : FoodSpotsSearchControllerSpec {
    @GetMapping("/search")
    override fun searchFoodSpots(
        user: User,
        foodSpotsSearchCondition: FoodSpotsSearchCondition,
    ) = foodSpotsSearchService.searchFoodSpots(user, foodSpotsSearchCondition)

    @GetMapping("/search/coin-required")
    override fun getRequiredCoin(
        user: User,
        requiredCoinRequest: RequiredCoinRequest,
    ) = foodSpotsSearchService.getRequiredCoin(user, requiredCoinRequest)
}
