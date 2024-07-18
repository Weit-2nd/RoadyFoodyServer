package kr.weit.roadyfoody.foodSpots.application.service

import kr.weit.roadyfoody.foodSpots.application.dto.FoodCategoryResponse
import kr.weit.roadyfoody.foodSpots.repository.FoodCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping

@Service
class FoodCategoriesQueryService(
    private val foodCategoryRepository: FoodCategoryRepository,
) {
    @GetMapping
    fun getCategories(): List<FoodCategoryResponse> = foodCategoryRepository.findAll().map(FoodCategoryResponse::of)
}
