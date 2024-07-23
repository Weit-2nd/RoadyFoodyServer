package kr.weit.roadyfoody.foodSpots.repository

import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.domain.ReportFoodCategory
import org.springframework.data.jpa.repository.JpaRepository

fun ReportFoodCategoryRepository.getByHistoryId(historyId: Long) = findByFoodSpotsHistoryId(historyId)

interface ReportFoodCategoryRepository : JpaRepository<ReportFoodCategory, Long> {
    fun findByFoodSpotsHistoryId(historyId: Long): List<ReportFoodCategory>

    fun deleteByFoodSpotsHistoryIn(histories: List<FoodSpotsHistory>)
}
