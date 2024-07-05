package kr.weit.roadyfoody.foodSpots.repository

import com.linecorp.kotlinjdsl.querymodel.jpql.expression.Expressions.customExpression
import com.linecorp.kotlinjdsl.querymodel.jpql.path.Paths.path
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicates.customPredicate
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import org.springframework.data.jpa.repository.JpaRepository

interface FoodSpotsRepository : JpaRepository<FoodSpots, Long>, CustomFoodSpotsRepository

interface CustomFoodSpotsRepository {
    fun findFoodSpotsByPointWithinRadius(
        centerLongitude: Double,
        centerLatitude: Double,
        radius: Int,
    ): List<FoodSpots>
}

class CustomFoodSpotsRepositoryImpl(
    private val executor: KotlinJdslJpqlExecutor,
) : CustomFoodSpotsRepository {
    // 카테고리 //가게이름
    override fun findFoodSpotsByPointWithinRadius(
        centerLongitude: Double,
        centerLatitude: Double,
        radius: Int,
    ): List<FoodSpots> {
        val distanceClause = "distance=$radius"
        val sdoGeometry = "SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE($centerLatitude, $centerLongitude, NULL), NULL, NULL)"
        val sdoWithinDistanceClause = "SDO_WITHIN_DISTANCE({0}, $sdoGeometry, '$distanceClause')"
        val sdoDistanceClause = "SDO_GEOM.SDO_DISTANCE({0}, $sdoGeometry, 1)"

        val sdoWithinDistance =
            customExpression(
                String::class,
                sdoWithinDistanceClause,
                listOf(
                    path(FoodSpots::point),
                ),
            )

        val sdoWithinDistancePredicate =
            customPredicate(
                "{0}  || '' = 'TRUE'",
                listOf(sdoWithinDistance),
            )

        val sdoDistance =
            customExpression(
                Double::class,
                sdoDistanceClause,
                listOf(path(FoodSpots::point)),
            )

        return executor.findAll {
            select(entity(FoodSpots::class))
                .from(entity(FoodSpots::class))
                .whereAnd(
                    path(FoodSpots::point).isNotNull(),
                    sdoWithinDistancePredicate,
                ).orderBy(sdoDistance.asc())
        } as List<FoodSpots>
    }
}
