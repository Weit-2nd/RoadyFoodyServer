package kr.weit.roadyfoody.foodSpots.utils

const val OPERATION_HOURS_REGEX_STR = "([01]?[0-9]|2[0-3]):([0-5][0-9])"
val OPERATION_HOURS_REGEX = Regex(OPERATION_HOURS_REGEX_STR)
const val OPERATION_HOURS_REGEX_DESC = "시간은 00:00부터 23:59까지의 형식이어야 합니다."
