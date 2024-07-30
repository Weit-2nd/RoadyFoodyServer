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
import kr.weit.roadyfoody.foodSpots.utils.OPERATION_HOURS_REGEX
import kr.weit.roadyfoody.foodSpots.utils.OPERATION_HOURS_REGEX_DESC

@Entity
@Table(
    name = "food_spots_operation_hours",
)
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
    var openingHours: String,
    @Column(nullable = false, length = 5)
    var closingHours: String,
) : BaseModifiableEntity() {
    init {
        require(OPERATION_HOURS_REGEX.matches(openingHours)) { OPERATION_HOURS_REGEX_DESC }
        require(OPERATION_HOURS_REGEX.matches(closingHours)) { OPERATION_HOURS_REGEX_DESC }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FoodSpotsOperationHours) return false
        if (foodSpots.id != other.foodSpots.id) return false
        if (dayOfWeek != other.dayOfWeek) return false
        if (openingHours != other.openingHours) return false
        if (closingHours != other.closingHours) return false

        return true
    }

    override fun hashCode(): Int {
        var result = foodSpots.id.hashCode()
        result = 31 * result + dayOfWeek.hashCode()
        result = 31 * result + openingHours.hashCode()
        result = 31 * result + closingHours.hashCode()
        return result
    }
}
