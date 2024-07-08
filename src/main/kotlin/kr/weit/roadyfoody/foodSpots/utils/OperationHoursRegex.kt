package kr.weit.roadyfoody.foodSpots.utils

const val OPERATION_HOURS_REGEX_STR = "([0-9]{1,2}):([0-9]{2})\\s*~\\s*([0-9]{1,2}):([0-9]{2})"
val OPERATION_HOURS_REGEX = Regex(OPERATION_HOURS_REGEX_STR)
const val OPERATION_HOURS_REGEX_DESC = "시간은 01:00부터 24:59까지의 형식이어야 합니다."
