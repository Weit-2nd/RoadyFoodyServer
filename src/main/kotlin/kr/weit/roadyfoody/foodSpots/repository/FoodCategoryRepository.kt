package kr.weit.roadyfoody.foodSpots.repository

import kr.weit.roadyfoody.foodSpots.domain.FoodCategory
import kr.weit.roadyfoody.foodSpots.exception.CategoriesNotFoundException
import org.springframework.data.jpa.repository.JpaRepository

fun FoodCategoryRepository.getFoodCategories(categoryIds: Set<Long>): List<FoodCategory> =
    findFoodCategoryByIdIn(categoryIds).also {
        if (it.size != categoryIds.size) throw CategoriesNotFoundException()
    }

interface FoodCategoryRepository : JpaRepository<FoodCategory, Long> {
    fun findFoodCategoryByIdIn(categoryIds: Set<Long>): List<FoodCategory>
}

