package kr.weit.roadyfoody.foodSpots.application.service

import kr.weit.roadyfoody.foodSpots.application.dto.FoodCategoryResponse
import kr.weit.roadyfoody.foodSpots.repository.FoodCategoryRepository
import org.springframework.stereotype.Service

@Service
class FoodCategoriesQueryService(
    private val foodCategoryRepository: FoodCategoryRepository,
) {
    fun getCategories(): List<FoodCategoryResponse> = foodCategoryRepository.findAll().map(FoodCategoryResponse::of)
}
