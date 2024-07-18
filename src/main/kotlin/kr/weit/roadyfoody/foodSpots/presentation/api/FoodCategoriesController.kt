package kr.weit.roadyfoody.foodSpots.presentation.api

import kr.weit.roadyfoody.foodSpots.application.service.FoodCategoriesQueryService
import kr.weit.roadyfoody.foodSpots.presentation.spec.FoodCategoriesControllerSpec
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/food-categories")
class FoodCategoriesController(
    private val foodCategoriesQueryService: FoodCategoriesQueryService,
) : FoodCategoriesControllerSpec {
    override fun getCategories() = foodCategoriesQueryService.getCategories()
}
