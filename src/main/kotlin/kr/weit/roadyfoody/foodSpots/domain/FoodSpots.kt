package kr.weit.roadyfoody.foodSpots.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import kr.weit.roadyfoody.common.domain.BaseModifiableEntity
import kr.weit.roadyfoody.global.utils.CoordinateUtils.Companion.createCoordinate
import org.locationtech.jts.geom.Point

@Entity
@Table(
    name = "food_spots",
    indexes = [
        Index(name = "food_spots_point_index", columnList = "point"),
        Index(name = "food_spots_name_index", columnList = "name"),
    ],
)
@SequenceGenerator(name = "FOOD_SPOTS_SEQ_GENERATOR", sequenceName = "FOOD_SPOTS_SEQ", initialValue = 1, allocationSize = 1)
class FoodSpots(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FOOD_SPOTS_SEQ_GENERATOR")
    val id: Long = 0L,
    @Column(length = 60, nullable = false)
    var name: String,
    @Column(nullable = false, updatable = false)
    val foodTruck: Boolean,
    @Column(nullable = false)
    var open: Boolean,
    @Column(nullable = false)
    var storeClosure: Boolean,
    @Column(columnDefinition = "SDO_GEOMETRY", nullable = false)
    var point: Point,
    @OneToMany(mappedBy = "foodSpots")
    val operationHoursList: MutableList<FoodSpotsOperationHours>,
    @OneToMany(mappedBy = "foodSpots")
    val foodCategoryList: MutableList<FoodSpotsFoodCategory>,
) : BaseModifiableEntity() {
    companion object {
        // SRID_WGS84: WGS84 좌표계
        const val SRID_WGS84 = 4326
    }

    constructor() : this(
        name = "default Name",
        foodTruck = false,
        open = false,
        storeClosure = false,
        point = createCoordinate(0.0, 0.0),
        operationHoursList = mutableListOf(),
        foodCategoryList = mutableListOf(),
    )
}
