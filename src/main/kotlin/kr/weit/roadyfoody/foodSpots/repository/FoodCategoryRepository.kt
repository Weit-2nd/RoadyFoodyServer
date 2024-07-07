package kr.weit.roadyfoody.foodSpots.repository

import kr.weit.roadyfoody.foodSpots.domain.FoodCategory
import kr.weit.roadyfoody.foodSpots.exception.NotFoundCategoriesException
import org.springframework.data.jpa.repository.JpaRepository

fun FoodCategoryRepository.getFoodCategories(categoryIds: Set<Long>): List<FoodCategory> =
    findFoodCategoryByIdIn(categoryIds).also {
        if (it.isEmpty()) throw NotFoundCategoriesException()
    }

interface FoodCategoryRepository : JpaRepository<FoodCategory, Long> {
    fun findFoodCategoryByIdIn(categoryIds: Set<Long>): List<FoodCategory>
}
