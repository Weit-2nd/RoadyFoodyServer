package kr.weit.roadyfoody.tourism.dto

data class SearchResponse(
    val title: String,
    val mainAddr: String?,
    val secondaryAddr: String?,
    val longitude: Double,
    val latitude: Double,
    val tel: String?,
    val thumbnailImage: String?,
    val tourismType: TourismType,
)

data class SearchResponses(val items: List<SearchResponse>)

enum class TourismType {
    TOUR,
    FESTIVAL,
}
