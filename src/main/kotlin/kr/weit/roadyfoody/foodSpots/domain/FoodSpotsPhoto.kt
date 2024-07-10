package kr.weit.roadyfoody.foodSpots.domain

import jakarta.persistence.Column
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
@Table(name = "food_spots_photos")
@SequenceGenerator(name = "FOOD_SPOTS_PHOTOS_SEQ_GENERATOR", sequenceName = "FOOD_SPOTS_PHOTOS_SEQ", initialValue = 1, allocationSize = 1)
class FoodSpotsPhoto(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FOOD_SPOTS_PHOTOS_SEQ_GENERATOR")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_spots_histories_id")
    val history: FoodSpotsHistory,
    @Column(nullable = false, length = 30)
    val fileName: String,
) : BaseTimeEntity() {
    companion object {
        fun of(
            history: FoodSpotsHistory,
            fileName: String,
        ) = FoodSpotsPhoto(
            history = history,
            fileName = fileName,
        )
    }
}
