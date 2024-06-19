package kr.weit.roadyfoody.foodSpots.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import kr.weit.roadyfoody.common.domain.BaseModifiableEntity

@Entity
@Table(name = "food_spots_operation_hours")
@SequenceGenerator(
    name = "FOOD_SPOTS_OPERATION_HOURS_SEQ_GENERATOR",
    sequenceName = "FOOD_SPOTS_OPERATION_HOURS_SEQ",
    initialValue = 1,
    allocationSize = 1,
)
@IdClass(FoodSpotsOperationHoursId::class)
class FoodSpotsOperationHours(
    @Id
    @ManyToOne
    @JoinColumn(name = "food_spots_id")
    val foodSpots: FoodSpots,
    @Id
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, length = 1)
    val dayOfWeek: DayOfWeek,
    @Column(nullable = false, length = 5)
    val openingHours: String,
    @Column(nullable = false, length = 5)
    val closingHours: String,
) : BaseModifiableEntity()
