package kr.weit.roadyfoody.global.utils

import kr.weit.roadyfoody.foodSpots.domain.FoodSpots.Companion.SRID_WGS84
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point

class CoordinateUtils {
    companion object {
        fun createCoordinate(
            longitude: Double,
            latitude: Double,
        ): Point =
            GeometryFactory().createPoint(Coordinate(longitude, latitude)).also {
                it.srid =
                    SRID_WGS84
            }
    }
}
