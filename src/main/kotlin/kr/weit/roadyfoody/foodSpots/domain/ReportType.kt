package kr.weit.roadyfoody.foodSpots.domain

enum class ReportType(
    val reportReward: ReportReward,
) {
    STORE_CREATE(ReportReward.CREATE_REWARD),
    STORE_UPDATE(ReportReward.UPDATE_REWARD),
    STORE_CLOSE(ReportReward.CLOSE_REWARD),
}
