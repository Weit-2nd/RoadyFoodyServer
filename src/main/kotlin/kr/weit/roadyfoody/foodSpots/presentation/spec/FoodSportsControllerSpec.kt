package kr.weit.roadyfoody.term.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import org.springframework.web.bind.annotation.RequestHeader

@Tag(name = SwaggerTag.TERM)
interface FoodSportsControllerSpec {
    @Operation(description = "음식점 리포트하는 API")
    fun createReport(
        @RequestHeader userId: Long,
        reportRequest: ReportRequest,
    )
}
