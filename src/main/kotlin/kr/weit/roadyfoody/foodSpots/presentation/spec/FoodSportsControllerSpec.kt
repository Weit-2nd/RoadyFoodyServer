package kr.weit.roadyfoody.foodSpots.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.common.exception.ErrorResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.validator.WebPImageList
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.multipart.MultipartFile

@Tag(name = SwaggerTag.FOOD_SPOTS)
interface FoodSportsControllerSpec {
    @Operation(
        description = "음식점 정보 리포트 API",
        parameters = [
            Parameter(name = "reportRequest", description = "음식점 정보", required = true),
            Parameter(
                name = "reportPhotos",
                description = "음식점 사진",
                required = false,
                content = [Content(mediaType = "image/webp")],
            ),
        ],
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "리포트 성공",
            ),
            ApiResponse(
                responseCode = "400",
                description = "리포트 실패",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Empty FoodSpot Name",
                                summary = "음식점 이름 미입력",
                                value = """
                        {
                            "code": -10000,
                            "errorMessage": "상호명은 1자 이상 20자 이하 한글, 영문, 숫자, 특수문자 여야 합니다."
                        }
                        """,
                            ),
                            ExampleObject(
                                name = "Invalid FoodSpot Name",
                                summary = "상호명에 특수문자가 포함된 경우",
                                value = """
                                {
                                    "code": -10000,
                                    "errorMessage": "상호명은 1자 이상 20자 이하 한글, 영문, 숫자, 특수문자 여야 합니다."
                                }
                                """,
                            ),
                            ExampleObject(
                                name = "FoodSpot Name Length Too Long",
                                summary = "상호명이 30자 초과인 경우",
                                value = """
                            {
                                "code": -10000,
                                "errorMessage": "상호명은 1자 이상 20자 이하 한글, 영문, 숫자, 특수문자 여야 합니다."
                            }
                            """,
                            ),
                            ExampleObject(
                                name = "latitude too high",
                                summary = "경도가 범위보다 높은 경우",
                                value = """
                            {
                                "code": -10000,
                                "errorMessage": "경도는 180 이하여야 합니다."
                            }
                            """,
                            ),
                            ExampleObject(
                                name = "latitude too low",
                                summary = "경도가 범위보다 낮은 경우",
                                value = """
                            {
                                "code": -10000,
                                "errorMessage": "경도는 -180 이상이어야 합니다."
                            }
                            """,
                            ),
                            ExampleObject(
                                name = "longitude too high",
                                summary = "위도가 범위보다 높은 경우",
                                value = """
                            {
                                "code": -10000,
                                "errorMessage": "위도는 180 이하여야 합니다."
                            }
                            """,
                            ),
                            ExampleObject(
                                name = "longitude too low",
                                summary = "위도가 범위보다 낮은 경우",
                                value = """
                            {
                                "code": -10000,
                                "errorMessage": "위도는 -180 이상여야 합니다."
                            }
                            """,
                            ),
                            ExampleObject(
                                name = "Too Many Images",
                                summary = "이미지가 3개 초과인 경우",
                                value = """
                        {
                            "code": -10000,
                            "errorMessage": "이미지는 최대 3개까지 업로드할 수 있습니다."
                        }
                        """,
                            ),
                            ExampleObject(
                                name = "Invalid Image type",
                                summary = "WEBP 이외의 이미지 입력",
                                value = """
                        {
                            "code": -10000,
                            "errorMessage": "하나 이상의 파일이 잘못 되었습니다."
                        }
                        """,
                            ),
                            ExampleObject(
                                name = "Too large image",
                                summary = "이미지 용량이 1MB 초과인 경우",
                                value = """
                        {
                            "code": -10000,
                            "errorMessage": "하나 이상의 파일이 잘못 되었습니다."
                        }
                        """,
                            ),
                        ],
                    ),
                ],
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
