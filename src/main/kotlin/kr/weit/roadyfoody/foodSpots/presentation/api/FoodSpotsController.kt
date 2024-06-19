package kr.weit.roadyfoody.term.presentation.api

import jakarta.validation.Valid
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.service.FoodSpotsService
import kr.weit.roadyfoody.term.presentation.spec.FoodSportsControllerSpec
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/food-spots")
class FoodSpotsController(
    private val foodSpotsService: FoodSpotsService,
) : FoodSportsControllerSpec {
    @PostMapping
    override fun createReport(
        @RequestHeader userId: Long,
        @Valid reportRequest: ReportRequest,
    ) = foodSpotsService.createReport(userId, reportRequest)
}
