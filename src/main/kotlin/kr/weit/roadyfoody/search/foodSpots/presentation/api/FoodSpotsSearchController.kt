package kr.weit.roadyfoody.search.foodSpots.presentation.api

import kr.weit.roadyfoody.search.foodSpots.application.FoodSpotsSearchService
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.presentation.spec.FoodSpotsSearchControllerSpec
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/food-spots")
class FoodSpotsSearchController(
    private val foodSpotsSearchService: FoodSpotsSearchService,
) : FoodSpotsSearchControllerSpec {
    @GetMapping("/search")
    override fun searchFoodSpots(foodSpotsSearchCondition: FoodSpotsSearchCondition) =
        foodSpotsSearchService.searchFoodSpots(foodSpotsSearchCondition)
}
