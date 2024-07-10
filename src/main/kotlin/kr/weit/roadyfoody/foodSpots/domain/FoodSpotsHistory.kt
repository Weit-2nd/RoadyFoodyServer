package kr.weit.roadyfoody.foodSpots.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import kr.weit.roadyfoody.common.domain.BaseTimeEntity
import kr.weit.roadyfoody.global.utils.CoordinateUtils.Companion.createCoordinate
import kr.weit.roadyfoody.user.domain.User
import org.locationtech.jts.geom.Point

@Entity
@Table(
    name = "food_spots_histories",
    indexes = [Index(name = "food_spots_histories_point_index", columnList = "point")],
)
@SequenceGenerator(
    name = "FOOD_SPOTS_HISTORIES_SEQ_GENERATOR",
    sequenceName = "FOOD_SPOTS_HISTORIES_SEQ",
    initialValue = 1,
    allocationSize = 1,
)
class FoodSpotsHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FOOD_SPOTS_HISTORIES_SEQ_GENERATOR")
    val id: Long = 0L,
    @ManyToOne
    @JoinColumn(updatable = false)
    val foodSpots: FoodSpots,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false)
    val user: User,
    @Column(nullable = false, updatable = false, length = 60)
    val name: String,
    @Column(nullable = false, updatable = false)
    val foodTruck: Boolean,
    @Column(nullable = false, updatable = false)
    val open: Boolean,
    @Column(nullable = false, updatable = false)
    val storeClosure: Boolean,
    @Column(columnDefinition = "SDO_GEOMETRY", nullable = false, updatable = false)
    val point: Point,
    @OneToMany(mappedBy = "foodSpotsHistory")
    val operationHoursList: List<ReportOperationHours>,
    @OneToMany(mappedBy = "foodSpotsHistory")
    val foodCategoryList: List<ReportFoodCategory>,
) : BaseTimeEntity() {
    constructor() : this(
        foodSpots = FoodSpots(),
        user = User.of("", "defaultNickname"),
        name = "defaultName",
        foodTruck = false,
        open = false,
        storeClosure = false,
        point = createCoordinate(0.0, 0.0),
        operationHoursList = emptyList(),
        foodCategoryList = emptyList(),
    )
}
