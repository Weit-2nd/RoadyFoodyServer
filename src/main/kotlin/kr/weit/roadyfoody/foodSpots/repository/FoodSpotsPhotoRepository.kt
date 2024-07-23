package kr.weit.roadyfoody.foodSpots.repository

import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsPhoto
import org.springframework.data.jpa.repository.JpaRepository

fun FoodSpotsPhotoRepository.getByHistoryId(historyId: Long): List<FoodSpotsPhoto> = findByHistoryId(historyId)

interface FoodSpotsPhotoRepository : JpaRepository<FoodSpotsPhoto, Long> {
    fun findByHistoryId(historyId: Long): List<FoodSpotsPhoto>

    fun findByHistoryIn(histories: List<FoodSpotsHistory>): List<FoodSpotsPhoto>
}
