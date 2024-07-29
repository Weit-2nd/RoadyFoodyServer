package kr.weit.roadyfoody.foodSpots.presentation.api

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsUpdateRequest
import kr.weit.roadyfoody.foodSpots.application.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsQueryService
import kr.weit.roadyfoody.foodSpots.presentation.spec.FoodSportsControllerSpec
import kr.weit.roadyfoody.foodSpots.validator.WebPImageList
import kr.weit.roadyfoody.user.domain.User
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/food-spots")
class FoodSpotsController(
    private val foodSpotsCommandService: FoodSpotsCommandService,
    private val foodSpotsQueryService: FoodSpotsQueryService,
) : FoodSportsControllerSpec {
    @ResponseStatus(CREATED)
    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    override fun createReport(
        @LoginUser
        user: User,
        @Valid
        @RequestPart
        reportRequest: ReportRequest,
        @Size(max = 3, message = "이미지는 최대 3개까지 업로드할 수 있습니다.")
        @WebPImageList
        @RequestPart(required = false)
        reportPhotos: List<MultipartFile>?,
    ) = foodSpotsCommandService.createReport(user, reportRequest, reportPhotos)

    @ResponseStatus(CREATED)
    @PatchMapping("/{foodSpotsId}")
    override fun updateFoodSpots(
        @LoginUser
        user: User,
        @Positive(message = "음식점 ID는 양수여야 합니다.")
        @PathVariable("foodSpotsId")
        foodSpotsId: Long,
        @Valid
        @RequestBody
        request: FoodSpotsUpdateRequest,
    ) {
        foodSpotsCommandService.doUpdateReport(user, foodSpotsId, request)
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/histories/{historyId}")
    override fun deleteFoodSpotsHistories(
        @LoginUser
        user: User,
        @Positive(message = "음식점 리포트 ID는 양수여야 합니다.")
        @PathVariable("historyId")
        historyId: Long,
    ) = foodSpotsCommandService.deleteFoodSpotsHistories(user, historyId)
}
