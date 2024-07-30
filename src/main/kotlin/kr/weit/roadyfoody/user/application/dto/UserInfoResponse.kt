package kr.weit.roadyfoody.user.application.dto

data class UserInfoResponse(
    val nickname: String,
    val profileImageUrl: String?,
    val coin: Int,
) {
    companion object {
        fun of(
            nickname: String,
            profileImageUrl: String?,
            coin: Int,
        ): UserInfoResponse =
            UserInfoResponse(
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                coin = coin,
            )
    }
}
