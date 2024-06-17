package kr.weit.roadyfoody.tourism.dto

data class SearchResponse(
    val title: String,
    val addr1: String?,
    val addr2: String?,
    val mapx: Double,
    val mapy: Double,
    val tel: String?,
    val firstimage2: String?,
    val type: TourismType,
)

data class SearchResponses(val items: List<SearchResponse>)

enum class TourismType {
    TOUR,
    FESTIVAL,
}
