package kr.weit.roadyfoody.tourism.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "검색 결과 항목")
data class SearchResponse(
    @Schema(description = "제목", example = "춘천아트센터")
    val title: String,
    @Schema(description = "주 주소", example = "강원특별자치도 춘천시 스포츠타운길399번길 25 KT&G상상마당 춘천아트센터")
    val mainAddr: String?,
    @Schema(description = "부 주소(거의 빈문자열)", example = "")
    val secondaryAddr: String?,
    @Schema(description = "경도", example = "126.981611")
    val longitude: Double,
    @Schema(description = "위도", example = "37.551169")
    val latitude: Double,
    @Schema(description = "전화번호", example = "1644-4845")
    val tel: String?,
    @Schema(description = "썸네일 이미지 URL", example = "http://tong.visitkorea.or.kr/cms/resource/67/2997767_image3_1.png")
    val thumbnailImage: String?,
    @Schema(description = "관광 타입", implementation = TourismType::class)
    val tourismType: TourismType,
)

@Schema(description = "검색 결과 응답")
data class SearchResponses(
    @Schema(description = "검색 결과 항목 리스트")
    val items: List<SearchResponse>,
)

@Schema(description = "관광 타입")
enum class TourismType {
    @Schema(description = "관광지")
    TOUR,

    @Schema(description = "축제/행사")
    FESTIVAL,
}
