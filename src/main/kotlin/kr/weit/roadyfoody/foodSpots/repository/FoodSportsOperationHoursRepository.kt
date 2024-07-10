package kr.weit.roadyfoody.foodSpots.repository

import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsOperationHours
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsOperationHoursId
import org.springframework.data.jpa.repository.JpaRepository

interface FoodSportsOperationHoursRepository : JpaRepository<FoodSpotsOperationHours, FoodSpotsOperationHoursId>
