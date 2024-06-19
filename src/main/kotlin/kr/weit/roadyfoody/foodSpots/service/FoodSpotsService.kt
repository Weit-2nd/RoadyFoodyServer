package kr.weit.roadyfoody.foodSpots.service

import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots.Companion.SRID_WGS84
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.stereotype.Service

@Service
class FoodSpotsService(
    private val foodSpotsRepository: FoodSpotsRepository,
) {
    fun createReport(
        userId: Long,
        reportRequest: ReportRequest,
    ) {
        val foodStoreInfo =
            FoodSpots(
                name = reportRequest.name,
                point =
                    GeometryFactory()
                        .createPoint(
                            Coordinate(reportRequest.longitude, reportRequest.latitude),
                        ).also { it.srid = SRID_WGS84 },
                foodTruck = reportRequest.foodTruck,
                open = reportRequest.open,
                storeClosure = reportRequest.closed,
            )
        foodSpotsRepository.save(foodStoreInfo)
    }
}
