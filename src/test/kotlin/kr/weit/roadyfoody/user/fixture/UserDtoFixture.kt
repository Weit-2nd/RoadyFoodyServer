package kr.weit.roadyfoody.user.fixture

import kr.weit.roadyfoody.user.application.dto.UserInfoResponse

fun createTestUserInfoResponse(
    nickname: String = TEST_USER_NICKNAME,
    profileImageUrl: String = TEST_USER_PROFILE_IMAGE_URL,
    coin: Int = TEST_USER_COIN,
) = UserInfoResponse(
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    coin = coin,
)
