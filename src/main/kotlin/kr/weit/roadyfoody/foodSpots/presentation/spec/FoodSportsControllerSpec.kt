package kr.weit.roadyfoody.foodSpots.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.validator.WebPImageList
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.multipart.MultipartFile

@Tag(name = SwaggerTag.FOOD_SPOTS)
interface FoodSportsControllerSpec {
    @Operation(
        description = "음식점 정보 리포트 API",
        parameters = [
            Parameter(name = "userId", description = "유저 ID", required = true, example = "1"),
            Parameter(name = "reportRequest", description = "음식점 정보", required = true),
            Parameter(
                name = "reportPhotos",
                description = "음식점 사진",
                required = false,
                content = [Content(mediaType = "image/webp")],
            ),
        ],
    )
    fun createReport(
        @RequestHeader
        userId: Long,
        @Valid
        reportRequest: ReportRequest,
        @Size(max = 3, message = "이미지는 최대 3개까지 업로드할 수 있습니다.")
        @WebPImageList
        reportPhotos: List<MultipartFile>?,
    )
}
