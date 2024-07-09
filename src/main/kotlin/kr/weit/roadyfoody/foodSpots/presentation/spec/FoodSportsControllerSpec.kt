package kr.weit.roadyfoody.foodSpots.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.ErrorResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportHistoriesResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.utils.SliceReportHistories
import kr.weit.roadyfoody.foodSpots.validator.WebPImageList
import kr.weit.roadyfoody.global.swagger.ApiErrorCodeExamples
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
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
            ApiResponse(
                responseCode = "404",
                description = "리포트 실패",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Not found user",
                                summary = "NOT_FOUND_USER",
                                value = """
                                {
                                    "code": -10009,
                                    "errorMessage": "10 ID 의 사용자는 존재하지 않습니다."
                                }
                            """,
                            ),
                        ],
                    ),
                ],
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
        description = "음식점 정보 리스트 조회 API",
        parameters = [
            Parameter(name = "userId", description = "유저 ID", required = true, example = "1"),
            Parameter(name = "size", description = "조회할 개수", required = false, example = "10"),
            Parameter(name = "lastId", description = "마지막으로 조회된 ID", required = false, example = "1"),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리포트 리스트 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema =
                            Schema(
                                implementation = SliceReportHistories::class,
                            ),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "리포트 리스트 조회 실패",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Not found user",
                                summary = "NOT_FOUND_USER",
                                value = """
                                {
                                    "code": -10009,
                                    "errorMessage": "10 ID 의 사용자는 존재하지 않습니다."
                                }
                            """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.SIZE_NON_POSITIVE,
            ErrorCode.LAST_ID_NON_POSITIVE,
        ],
    )
    fun getReportHistories(
        @PathVariable("userId")
        userId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @Positive(message = "마지막 ID는 양수여야 합니다.")
        @RequestParam(required = false)
        lastId: Long?,
    ): SliceResponse<ReportHistoriesResponse>
}
