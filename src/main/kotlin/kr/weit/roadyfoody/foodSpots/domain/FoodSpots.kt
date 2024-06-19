package kr.weit.roadyfoody.foodSpots.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import kr.weit.roadyfoody.common.domain.BaseModifiableEntity
import kr.weit.roadyfoody.foodSpots.utils.FOOD_SPOTS_NAME_REGEX
import kr.weit.roadyfoody.foodSpots.utils.FOOD_SPOTS_NAME_REGEX_DESC
import org.locationtech.jts.geom.Point

@Entity
@Table(
    name = "food_spots",
    indexes = [Index(name = "food_spots_point_index", columnList = "point")],
)
@SequenceGenerator(name = "FOOD_SPOTS_SEQ_GENERATOR", sequenceName = "FOOD_SPOTS_SEQ", initialValue = 1, allocationSize = 1)
class FoodSpots(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FOOD_SPOTS_SEQ_GENERATOR")
    val id: Long = 0L,
    @Column(length = 20, nullable = false)
    var name: String,
    @Column(nullable = false, updatable = false)
    val foodTruck: Boolean,
    @Column(nullable = false)
    var open: Boolean,
    @Column(nullable = false)
    var storeClosure: Boolean,
    @Column(columnDefinition = "SDO_GEOMETRY", nullable = false)
    var point: Point,
) : BaseModifiableEntity() {
    init {
        require(FOOD_SPOTS_NAME_REGEX.matches(name)) { FOOD_SPOTS_NAME_REGEX_DESC }
    }

    companion object {
        // SRID_WGS84: WGS84 좌표계
        const val SRID_WGS84 = 4326
    }
}
