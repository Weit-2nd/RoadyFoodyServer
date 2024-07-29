package kr.weit.roadyfoody.search.address.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.foodSpots.validator.Latitude
import kr.weit.roadyfoody.foodSpots.validator.Longitude
import kr.weit.roadyfoody.global.swagger.ApiErrorCodeExamples
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.search.address.dto.AddressSearchResponse
import kr.weit.roadyfoody.search.address.dto.AddressSearchResponses
import kr.weit.roadyfoody.search.address.dto.Point2AddressResponse
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = SwaggerTag.SEARCH)
interface AddressSearchControllerSpec {
    @Operation(
        description = "주소 검색 API",
        parameters = [
            Parameter(name = "numOfRows", description = "반환받을 데이터 수", required = true, example = "10"),
            Parameter(name = "keyword", description = "검색할 키워드", required = true, example = "명륜진사갈비"),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = AddressSearchResponse::class),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.REST_CLIENT_ERROR,
            ErrorCode.SIZE_NON_POSITIVE,
            ErrorCode.SEARCH_KEYWORD_LENGTH,
        ],
    )
    fun searchAddress(
        @RequestParam
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        numOfRows: Int,
        @Size(min = 1, max = 60, message = "검색어는 1자 이상 60자 이하로 입력해주세요.")
        @RequestParam keyword: String,
    ): AddressSearchResponses

    @Operation(
        description = "좌표 검색 API",
        parameters = [
            Parameter(name = "longitude", description = "경도", required = true, example = "127.423084873712"),
            Parameter(name = "latitude", description = "위도", required = true, example = "37.0789561558879"),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Point2AddressResponse::class),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.REST_CLIENT_ERROR,
            ErrorCode.LATITUDE_TOO_HIGH,
            ErrorCode.LATITUDE_TOO_LOW,
            ErrorCode.LONGITUDE_TOO_HIGH,
            ErrorCode.LONGITUDE_TOO_LOW,
        ],
    )
    fun searchPoint2Address(
        @Schema(description = "경도", example = "127.12312219099")
        @NotNull(message = "경도는 필수입니다.")
        @Longitude
        longitude: Double,
        @Schema(description = "위도", example = "37.4940529587731")
        @NotNull(message = "위도는 필수입니다.")
        @Latitude
        latitude: Double,
    ): Point2AddressResponse
}
