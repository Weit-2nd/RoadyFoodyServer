package kr.weit.roadyfoody.user.application.dto

import io.swagger.v3.oas.annotations.media.Schema

data class UserInfoResponse(
    @Schema(description = "유저 닉네임", example = "TestNickname")
    val nickname: String,
    @Schema(description = "프로필 URL")
    val profileImageUrl: String?,
    @Schema(description = "뱃지 정보", example = "초심자")
    val badge: String,
    @Schema(description = "보유 중인 코인", example = "100")
    val coin: Int,
    @Schema(description = "잔여 당일 리포트 생성 횟수", example = "3")
    val restDailyReportCreationCount: Int,
) {
    companion object {
        fun of(
            nickname: String,
            profileImageUrl: String?,
            badge: String,
            coin: Int,
            restDailyReportCreationCount: Int,
        ): UserInfoResponse =
            UserInfoResponse(
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                badge = badge,
                coin = coin,
                restDailyReportCreationCount = restDailyReportCreationCount,
            )
    }
}

@Schema(description = "보유중인 코인 응답 데이터")
data class UserCoinBalance(
    @Schema(description = "보유중인 코인")
    val userCoin: Int,
)
