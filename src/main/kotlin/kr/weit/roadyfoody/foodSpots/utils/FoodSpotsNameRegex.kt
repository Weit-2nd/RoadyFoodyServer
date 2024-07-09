package kr.weit.roadyfoody.foodSpots.utils

const val FOOD_SPOTS_NAME_MAX_LENGTH = 20
const val FOOD_SPOTS_NAME_REGEX_STR = "^[가-힣a-zA-Z0-9.,'·&\\-\\s]{1,${FOOD_SPOTS_NAME_MAX_LENGTH}}\$"
val FOOD_SPOTS_NAME_REGEX = Regex(FOOD_SPOTS_NAME_REGEX_STR)
const val FOOD_SPOTS_NAME_REGEX_DESC = "상호명은 1자 이상 ${FOOD_SPOTS_NAME_MAX_LENGTH}자 이하 한글, 영문, 숫자, 특수문자 여야 합니다."
