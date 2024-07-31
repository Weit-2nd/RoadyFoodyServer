package kr.weit.roadyfoody.foodSpots.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsReviewResponse
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsUpdateRequest
import kr.weit.roadyfoody.foodSpots.application.dto.ReportHistoryDetailResponse
import kr.weit.roadyfoody.foodSpots.application.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.utils.SliceFoodSpotsReview
import kr.weit.roadyfoody.foodSpots.validator.WebPImageList
import kr.weit.roadyfoody.global.swagger.ApiErrorCodeExamples
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.review.repository.ReviewSortType
import kr.weit.roadyfoody.user.domain.User
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Tag(name = SwaggerTag.FOOD_SPOTS)
interface FoodSportsControllerSpec {
    @Operation(
        description = "음식점 정보 리포트 API",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "리포트 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_LENGTH_FOOD_SPOTS_NAME,
            ErrorCode.INVALID_CHARACTERS_FOOD_SPOTS_NAME,
            ErrorCode.LATITUDE_TOO_HIGH,
            ErrorCode.LATITUDE_TOO_LOW,
            ErrorCode.LONGITUDE_TOO_HIGH,
            ErrorCode.LONGITUDE_TOO_LOW,
            ErrorCode.NO_CATEGORY_SELECTED,
            ErrorCode.INVALID_FORMAT_OPERATION_HOURS,
            ErrorCode.IMAGES_TOO_MANY,
            ErrorCode.INVALID_IMAGE_TYPE,
            ErrorCode.IMAGES_SIZE_TOO_LARGE,
            ErrorCode.NOT_FOUND_FOOD_CATEGORY,
        ],
    )
    fun createReport(
        @LoginUser
        user: User,
        @Valid
        @RequestPart
        reportRequest: ReportRequest,
        @Size(max = 3, message = "이미지는 최대 3개까지 업로드할 수 있습니다.")
        @WebPImageList
        @RequestPart(required = false)
        reportPhotos: List<MultipartFile>?,
    )

    @Operation(
        description = "음식점 정보 수정 API",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "음식점 정보 수정 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_LENGTH_FOOD_SPOTS_NAME,
            ErrorCode.INVALID_CHARACTERS_FOOD_SPOTS_NAME,
            ErrorCode.LATITUDE_TOO_HIGH,
            ErrorCode.LATITUDE_TOO_LOW,
            ErrorCode.LONGITUDE_TOO_HIGH,
            ErrorCode.LONGITUDE_TOO_LOW,
            ErrorCode.NO_CATEGORY_SELECTED,
            ErrorCode.INVALID_FORMAT_OPERATION_HOURS,
            ErrorCode.NOT_FOUND_FOOD_CATEGORY,
            ErrorCode.INVALID_CHANGE_VALUE,
            ErrorCode.NON_POSITIVE_FOOD_SPOT_ID,
        ],
    )
    fun updateFoodSpots(
        user: User,
        @Positive(message = "음식점 ID는 양수여야 합니다.")
        @Parameter(description = "음식점 ID", required = true, example = "1")
        foodSpotsId: Long,
        @Valid
        request: FoodSpotsUpdateRequest,
    )

    @Operation(
        description = "음식점 정보 리포트 삭제 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "리포트 삭제 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_FOUND_FOOD_SPOTS_HISTORIES,
            ErrorCode.NON_POSITIVE_FOOD_SPOTS_HISTORIES_ID,
            ErrorCode.NOT_FOOD_SPOTS_HISTORIES_OWNER,
        ],
    )
    fun deleteFoodSpotsHistories(
        @LoginUser
        user: User,
        @Positive(message = "음식점 리포트 ID는 양수여야 합니다.")
        @PathVariable("historyId")
        historyId: Long,
    )

    @Operation(
        description = "음식점 리포트 이력 상세 조회 API",
        parameters = [
            Parameter(name = "historyId", description = "조회할 리포트 이력 ID", example = "1"),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리포트 상세 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ReportHistoryDetailResponse::class),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NON_POSITIVE_FOOD_SPOTS_HISTORIES_ID,
            ErrorCode.NOT_FOUND_FOOD_SPOTS_HISTORIES,
        ],
    )
    fun getReportHistory(
        @PathVariable("historyId")
        @Positive
        historyId: Long,
    ): ReportHistoryDetailResponse

    @Operation(
        description = "음식점의 리뷰 리스트 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리뷰 리스트 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema =
                            Schema(
                                implementation = SliceFoodSpotsReview::class,
                            ),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.SIZE_NON_POSITIVE,
            ErrorCode.LAST_ID_NON_POSITIVE,
            ErrorCode.FOOD_SPOT_ID_NON_POSITIVE,
        ],
    )
    fun getFoodSpotsReviews(
        @PathVariable("foodSpotsId")
        @Positive(message = "음식점 ID는 양수여야 합니다.")
        foodSpotsId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @Positive(message = "마지막 ID는 양수여야 합니다.")
        @RequestParam(required = false)
        lastId: Long?,
        @RequestParam(defaultValue = "LATEST", required = false)
        sortType: ReviewSortType,
    ): SliceResponse<FoodSpotsReviewResponse>
}
