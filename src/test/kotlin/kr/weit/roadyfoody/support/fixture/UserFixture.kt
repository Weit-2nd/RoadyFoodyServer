package kr.weit.roadyfoody.support.fixture

import kr.weit.roadyfoody.domain.user.User

const val TEST_USER_1_NICKNAME = "existentNick"

fun createTestUser1() = User(nickname = TEST_USER_1_NICKNAME)

const val TEST_NONEXISTENT_ID = 0L
const val TEST_NONEXISTENT_NICKNAME = "JohnDoe"
