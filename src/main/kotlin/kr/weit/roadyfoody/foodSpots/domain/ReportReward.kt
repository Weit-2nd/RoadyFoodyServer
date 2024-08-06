package kr.weit.roadyfoody.foodSpots.domain

enum class ReportReward(
    val point: Int,
) {
    CREATE_REWARD(200),
    UPDATE_REWARD(100),
    CLOSE_REWARD(150),
}
