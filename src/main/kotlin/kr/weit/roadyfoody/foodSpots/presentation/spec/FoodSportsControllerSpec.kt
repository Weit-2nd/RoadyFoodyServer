package kr.weit.roadyfoody.foodSpots.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.tags.Tag
import kr.weit.roadyfoody.foodSpots.dto.ReportHistoriesResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistorySortType
import kr.weit.roadyfoody.global.dto.SliceResponse
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import org.springframework.web.multipart.MultipartFile

@Tag(name = SwaggerTag.FOOD_SPOTS)
interface FoodSportsControllerSpec {
    // TODO: objectStorage를 사용하여 이미지 저장하도록 수정
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
        userId: Long,
        reportRequest: ReportRequest,
        reportPhotos: List<MultipartFile>,
    )

    // TODO: 음식점 정보 리스트 피그마 나올시, 반환하는 값 수정(현재 다 이미지빼고 반환됨)
    @Operation(
        description = "음식점 정보 리스트 조회 API",
        parameters = [
            Parameter(name = "userId", description = "유저 ID", required = true, example = "1"),
            Parameter(name = "size", description = "조회할 개수", required = false, example = "10"),
            Parameter(name = "lastId", description = "마지막 ID", required = false, example = "1"),
            Parameter(name = "sortType", description = "정렬 타입", required = false, example = "LATEST"),
        ],
    )
    fun getFoodSpotsReportHistories(
        userId: Long,
        size: Int,
        sortType: FoodSpotsHistorySortType,
        lastId: Long?,
    ): SliceResponse<ReportHistoriesResponse>

    @Operation(
        description = "음식점 정보 조회 API",
        parameters = [
            Parameter(name = "userId", description = "유저 ID", required = true, example = "1"),
            Parameter(name = "size", description = "조회할 개수", required = false, example = "10"),
            Parameter(name = "lastId", description = "마지막 ID", required = false, example = "1"),
            Parameter(name = "sortType", description = "정렬 타입", required = false, example = "LATEST"),
        ],
    )
    fun getFoodSpotReport(
        userId: Long,
        foodSpotsHistoryId: Long,
    ): ReportHistoriesResponse
    // TODO: 음식점 정보 조회 피그마 나올시, 반환하는 값 수정(현재 다 반환됨)
}
