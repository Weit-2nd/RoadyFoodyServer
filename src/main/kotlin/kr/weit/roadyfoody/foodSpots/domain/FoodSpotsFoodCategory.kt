package kr.weit.roadyfoody.foodSpots.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import kr.weit.roadyfoody.common.domain.BaseTimeEntity

@Entity
@Table(
    name = "food_spots_food_categories",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["food_spots_id", "food_categories_id"],
            name = "food_spots_food_categories_unique_constraint",
        ),
    ],
)
@SequenceGenerator(
    name = "FOOD_SPOTS_FOOD_CATEGORIES_SEQ_GENERATOR",
    sequenceName = "FOOD_SPOTS_FOOD_CATEGORIES_SEQ",
    initialValue = 1,
    allocationSize = 1,
)
class FoodSpotsFoodCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FOOD_SPOTS_FOOD_CATEGORIES_SEQ_GENERATOR")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_spots_id", nullable = false, updatable = false)
    val foodSpots: FoodSpots,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_categories_id")
    val foodCategory: FoodCategory,
) : BaseTimeEntity() {
    constructor(foodSpots: FoodSpots, foodCategory: FoodCategory, id: Long = 0L) : this(
        id,
        foodSpots,
        foodCategory,
    )
}
