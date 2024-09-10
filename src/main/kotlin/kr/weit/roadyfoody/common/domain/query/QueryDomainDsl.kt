@file:Suppress("ktlint:standard:filename")

package kr.weit.roadyfoody.common.domain.query

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entity
import com.linecorp.kotlinjdsl.querymodel.jpql.expression.Expression
import com.linecorp.kotlinjdsl.querymodel.jpql.expression.Expressions
import com.linecorp.kotlinjdsl.querymodel.jpql.path.Paths
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicates
import kr.weit.roadyfoody.foodSpots.domain.FoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsFoodCategory

class SearchDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<SearchDsl> {
        override fun newInstance(): SearchDsl = SearchDsl()
    }

    fun Entity<FoodSpots>.foodSpotIdIn(ids: List<Long>): Predicate = path(FoodSpots::id).`in`(ids)

    fun withinDistance(
        radius: Int,
        centerLongitude: Double,
        centerLatitude: Double,
    ): Predicate {
        val distanceClause = "distance=$radius"
        val sdoGeometry =
            "SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE($centerLongitude, $centerLatitude, NULL), NULL, NULL)"
        val sdoWithinDistanceClause = "SDO_WITHIN_DISTANCE({0}, $sdoGeometry, '$distanceClause')"

        val sdoWithinDistance =
            Expressions.customExpression(
                String::class,
                sdoWithinDistanceClause,
                listOf(
                    Paths.path(FoodSpots::point),
                ),
            )

        val sdoWithinDistancePredicate =
            Predicates.customPredicate(
                "{0}  || '' = 'TRUE'",
                listOf(sdoWithinDistance),
            )

        return sdoWithinDistancePredicate
    }

    fun getDistance(
        centerLongitude: Double,
        centerLatitude: Double,
    ): Expression<Double> {
        val sdoGeometry =
            "SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE($centerLongitude, $centerLatitude, NULL), NULL, NULL)"
        val sdoDistanceClause = "SDO_GEOM.SDO_DISTANCE({0}, $sdoGeometry, 1)"

        return Expressions.customExpression(
            Double::class,
            sdoDistanceClause,
            listOf(
                Paths.path(FoodSpots::point),
            ),
        )
    }

    fun containsName(name: String): Expression<Int> =
        Expressions
            .customExpression(
                Int::class,
                "myContains({0}, {1})",
                listOf(
                    path(FoodSpots::name),
                    Expressions.stringLiteral("%$name%"),
                ),
            )

    fun Entity<FoodSpotsFoodCategory>.foodCategoryIn(values: List<Long>): Predicate =
        path(FoodSpotsFoodCategory::foodCategory)(FoodCategory::id).`in`(values)
}
