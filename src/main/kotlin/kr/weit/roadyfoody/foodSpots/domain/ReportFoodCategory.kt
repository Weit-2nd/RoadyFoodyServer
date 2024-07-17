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
import kr.weit.roadyfoody.common.domain.BaseTimeEntity

@Entity
@Table(
    name = "report_food_categories",
)
@SequenceGenerator(
    name = "REPORT_FOOD_CATEGORIES_SEQ_GENERATOR",
    sequenceName = "REPORT_FOOD_CATEGORIES_SEQ",
    initialValue = 1,
    allocationSize = 1,
)
class ReportFoodCategory(
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "REPORT_FOOD_CATEGORIES_SEQ_GENERATOR",
    )
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_spots_history_id")
    val foodSpotsHistory: FoodSpotsHistory,
    @ManyToOne
    @JoinColumn(name = "food_categories_id")
    val foodCategory: FoodCategory,
) : BaseTimeEntity() {
    constructor(
        foodSpotsHistory: FoodSpotsHistory,
        foodCategory: FoodCategory,
        id: Long = 0L,
    ) : this(id, foodSpotsHistory, foodCategory)
}
