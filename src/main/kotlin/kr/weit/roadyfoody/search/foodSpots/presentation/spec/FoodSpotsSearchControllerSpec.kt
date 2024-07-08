package kr.weit.roadyfoody.search.foodSpots.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.global.swagger.ApiErrorCodeExamples
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponses
import org.springframework.web.bind.annotation.ModelAttribute

@Tag(name = SwaggerTag.SEARCH)
interface FoodSpotsSearchControllerSpec {
    @Operation(
        description = "가게 조회 API - 지도 용",
        parameters = [
            Parameter(name = "centerLongitude", description = "지도 중심 경도", required = true, example = "127.074667"),
            Parameter(name = "centerLatitude", description = "지도 중심 위도", required = true, example = "37.147030"),
            Parameter(name = "radius", description = "검색 반경", required = true, example = "500"),
            Parameter(name = "name", description = "가게 이름", required = false, example = "pot2"),
            Parameter(name = "categoryIds", description = "음식 카테고리 ID를 ,로 묶어서 보내주세요. (1,2)", required = false, example = "1,2"),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.RADIUS_SIZE_TOO_SMALL,
            ErrorCode.LATITUDE_TOO_LOW,
            ErrorCode.LONGITUDE_TOO_LOW,
            ErrorCode.LATITUDE_TOO_HIGH,
            ErrorCode.LONGITUDE_TOO_HIGH,
        ],
    )
    fun searchFoodSpots(
        @ModelAttribute @Valid foodSpotsSearchCondition: FoodSpotsSearchCondition,
    ): FoodSpotsSearchResponses
}
