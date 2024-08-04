package kr.weit.roadyfoody.foodSpots.domain

enum class ReportType(
    val rewardPoint: Int,
) {
    STORE_CREATE(200),
    STORE_UPDATE(100),
    STORE_CLOSE(150),
}
