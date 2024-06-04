package kr.weit.roadyfoody.support.fixture

import kr.weit.roadyfoody.domain.user.User
import kr.weit.roadyfoody.support.regex.NICKNAME_MAX_LENGTH
import kr.weit.roadyfoody.support.regex.NICKNAME_MIN_LENGTH

const val TEST_USER_1_NICKNAME = "existentNick"

fun createTestUser1() = User(nickname = TEST_USER_1_NICKNAME)

val TEST_MIN_LENGTH_NICKNAME = (0 until NICKNAME_MIN_LENGTH).map { 'a' + it }.joinToString("")
val TEST_MAX_LENGTH_NICKNAME = (0 until NICKNAME_MAX_LENGTH).map { 'a' + it }.joinToString("")

const val TEST_NONEXISTENT_ID = 0L
const val TEST_NONEXISTENT_NICKNAME = "JohnDoe"
