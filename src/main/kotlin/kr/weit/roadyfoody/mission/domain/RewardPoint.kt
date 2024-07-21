package kr.weit.roadyfoody.mission.domain

enum class RewardPoint(
    point: Int,
) {
    REPORT(100),
    FIRST_REPORT(200),
    CLOSED_REPORT(150),
    ;

    val point = point
}
