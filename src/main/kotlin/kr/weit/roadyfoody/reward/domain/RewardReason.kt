package kr.weit.roadyfoody.reward.domain

enum class RewardReason(
    val point: Int
) {
    REPORT_CREATE(200),
    REPORT_UPDATE(100),
    REPORT_CLOSE(150),
    GUERRILLA_MISSION(150),
    //todo: 뱃지 갱신시 코인 지급 - 뱃지 작업시 변경
    BADGE_UPDATE(0),
    SEARCH_SPOT(0),
    REPORT_DELETE(0),
}