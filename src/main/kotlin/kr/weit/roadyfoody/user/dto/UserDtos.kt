package kr.weit.roadyfoody.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.user.domain.User

data class UserSimpleInfoResponse(
    @Schema(description = "유저 id", example = "1")
    val id: Long,
    @Schema(description = "유저 닉네임", example = "TestNickname")
    val nickname: String,
    @Schema(description = "프로필 URL")
    val url: String?,
) {
    companion object {
        fun from(
            user: User,
            url: String?,
        ) = UserSimpleInfoResponse(user.id, user.profile.nickname, url)
    }
}
