package kr.weit.roadyfoody.user.fixture

import kr.weit.roadyfoody.user.domain.Profile
import kr.weit.roadyfoody.user.domain.SocialLoginType
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.utils.NICKNAME_MAX_LENGTH
import kr.weit.roadyfoody.user.utils.NICKNAME_MIN_LENGTH

val TEST_MIN_LENGTH_NICKNAME = (0 until NICKNAME_MIN_LENGTH).map { 'a' + it }.joinToString("")
val TEST_MAX_LENGTH_NICKNAME = (0 until NICKNAME_MAX_LENGTH).map { 'a' + it }.joinToString("")

const val TEST_USER_ID = 1L
const val TEST_SOCIAL_ID = 12345678L
val TEST_SOCIAL_LOGIN_TYPE = SocialLoginType.KAKAO
val TEST_USER_SOCIAL_ID = "$TEST_SOCIAL_LOGIN_TYPE $TEST_SOCIAL_ID"
const val TEST_USER_NICKNAME = "existentNick"
const val TEST_USER_PROFILE_IMAGE_NAME = "test_image_name"

fun createTestUser(id: Long = TEST_USER_ID) =
    User(
        id,
        TEST_USER_SOCIAL_ID,
        Profile(
            "$TEST_USER_NICKNAME$id",
            "${TEST_USER_PROFILE_IMAGE_NAME}_$id",
        ),
    )

fun createTestUser(
    nickname: String = TEST_USER_NICKNAME,
    socialId: String = TEST_USER_SOCIAL_ID,
) = User.of(
    socialId,
    nickname,
    TEST_USER_PROFILE_IMAGE_NAME,
)

// fail case
const val TEST_NONEXISTENT_ID = 0L
const val TEST_NONEXISTENT_NICKNAME = "JohnDoe"
