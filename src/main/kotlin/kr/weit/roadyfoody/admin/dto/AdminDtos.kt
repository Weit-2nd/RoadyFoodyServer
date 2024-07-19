package kr.weit.roadyfoody.admin.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.user.domain.User

data class SimpleUserInfoResponse(
    @Schema(description = "유저 아이디", example = "1")
    val userId: Long,
    @Schema(description = "유저 닉네임", example = "유저1")
    val nickname: String,
    @Schema(description = "유저 코인 보유량", example = "100")
    val coin: Int,
) {
    companion object {
        fun from(user: User) = SimpleUserInfoResponse(user.id, user.profile.nickname, user.coin)
    }
}

data class SimpleUserInfoResponses(
    @Schema(description = "유저 정보")
    val userInfo: List<SimpleUserInfoResponse>,
)

data class UserAccessTokenResponse(
    @Schema(description = "유저 AccessToken")
    val accessToken: String,
)
