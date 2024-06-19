package kr.weit.roadyfoody.foodSpots.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "food_categories")
@SequenceGenerator(name = "FOOD_CATEGORY_SEQ_GENERATOR", sequenceName = "FOOD_CATEGORY_SEQ", initialValue = 1, allocationSize = 1)
class FoodCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FOOD_CATEGORY_SEQ_GENERATOR")
    val id: Long = 0L,
    @Column(nullable = false, length = 30)
    val name: String,
)
