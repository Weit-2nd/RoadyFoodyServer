package kr.weit.roadyfoody.foodSpots.presentation.api

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.foodSpots.dto.ReportHistoriesResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.presentation.spec.FoodSportsControllerSpec
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistorySortType
import kr.weit.roadyfoody.foodSpots.service.FoodSpotsService
import kr.weit.roadyfoody.global.dto.SliceResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Validated
@RestController
@RequestMapping("/api/v1/food-spots")
class FoodSpotsController(
    private val foodSpotsService: FoodSpotsService,
) : FoodSportsControllerSpec {
    @PostMapping
    override fun createReport(
        @RequestHeader userId: Long,
        @Valid
        @RequestPart
        reportRequest: ReportRequest,
        @Size(max = 3, message = "이미지는 최대 3개까지 업로드할 수 있습니다.")
        @RequestPart("reportPhotos")
        reportPhotos: List<MultipartFile>,
    ) = foodSpotsService.createReport(userId, reportRequest, reportPhotos)

    override fun getFoodSpotsReportHistories(
        userId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam("size", defaultValue = "10", required = false)
        size: Int,
        @RequestParam("sortType", defaultValue = "LATEST", required = false)
        sortType: FoodSpotsHistorySortType,
        @Positive(message = "마지막 ID는 양수여야 합니다.")
        @RequestParam("lastId", required = false)
        lastId: Long?,
    ): SliceResponse<ReportHistoriesResponse> = foodSpotsService.getFoodSpots(userId, size, sortType, lastId)
}
