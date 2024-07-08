package kr.weit.roadyfoody.foodSpots.repository

import kr.weit.roadyfoody.foodSpots.domain.FoodCategory
import org.springframework.data.jpa.repository.JpaRepository

interface FoodCategoryRepository : JpaRepository<FoodCategory, Long>
