package kr.weit.roadyfoody.foodSpots.repository

import kr.weit.roadyfoody.foodSpots.domain.ReportOperationHours
import kr.weit.roadyfoody.foodSpots.domain.ReportOperationHoursId
import org.springframework.data.jpa.repository.JpaRepository

interface ReportOperationHoursRepository : JpaRepository<ReportOperationHours, ReportOperationHoursId>
