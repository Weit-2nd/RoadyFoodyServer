package kr.weit.roadyfoody.user.fixture

import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.utils.NICKNAME_MAX_LENGTH
import kr.weit.roadyfoody.user.utils.NICKNAME_MIN_LENGTH

val TEST_MIN_LENGTH_NICKNAME = (0 until NICKNAME_MIN_LENGTH).map { 'a' + it }.joinToString("")
val TEST_MAX_LENGTH_NICKNAME = (0 until NICKNAME_MAX_LENGTH).map { 'a' + it }.joinToString("")

const val TEST_USER_ID = 1L
const val TEST_USER_NICKNAME = "existentNick"

fun createTestUser(id: Long) = User(id, "$TEST_USER_NICKNAME$id")

// fail case
const val TEST_NONEXISTENT_ID = 0L
const val TEST_NONEXISTENT_NICKNAME = "JohnDoe"
