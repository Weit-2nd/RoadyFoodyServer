package kr.weit.roadyfoody.foodSpots.presentation.api

import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.presentation.spec.FoodSportsControllerSpec
import kr.weit.roadyfoody.foodSpots.service.FoodSpotsService
import kr.weit.roadyfoody.foodSpots.validator.WebPImageList
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/food-spots")
class FoodSpotsController(
    private val foodSpotsService: FoodSpotsService,
) : FoodSportsControllerSpec {
    @ResponseStatus(CREATED)
    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    override fun createReport(
        @RequestHeader
        userId: Long,
        @Valid
        @RequestPart
        reportRequest: ReportRequest,
        @Size(max = 3, message = "이미지는 최대 3개까지 업로드할 수 있습니다.")
        @WebPImageList
        @RequestPart(required = false)
        reportPhotos: List<MultipartFile>?,
    ) = foodSpotsService.createReport(userId, reportRequest, reportPhotos)
}
