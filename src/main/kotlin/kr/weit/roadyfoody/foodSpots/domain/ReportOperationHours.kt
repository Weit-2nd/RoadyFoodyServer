package kr.weit.roadyfoody.foodSpots.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import kr.weit.roadyfoody.common.domain.BaseTimeEntity

@Entity
@Table(
    name = "report_operation_hours",
)
@IdClass(ReportOperationHoursId::class)
class ReportOperationHours(
    @Id
    @ManyToOne
    @JoinColumn(name = "food_spots_history_id")
    val foodSpotsHistory: FoodSpotsHistory,
    @Id
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, length = 1)
    val dayOfWeek: DayOfWeek,
    @Column(nullable = false, length = 5)
    val openingHours: String,
    @Column(nullable = false, length = 5)
    val closingHours: String,
) : BaseTimeEntity()
