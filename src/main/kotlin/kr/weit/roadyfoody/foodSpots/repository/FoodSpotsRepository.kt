package kr.weit.roadyfoody.foodSpots.repository

import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import org.springframework.data.jpa.repository.JpaRepository

interface FoodSpotsRepository : JpaRepository<FoodSpots, Long>
