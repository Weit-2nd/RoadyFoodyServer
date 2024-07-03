package kr.weit.roadyfoody.user.utils

const val NICKNAME_MIN_LENGTH = 6
const val NICKNAME_MAX_LENGTH = 16
const val NICKNAME_REGEX_STR = "^[ㄱ-ㅎㅏ-ㅣ가-힣A-Za-z0-9]{$NICKNAME_MIN_LENGTH,$NICKNAME_MAX_LENGTH}\$"
val NICKNAME_REGEX = Regex(NICKNAME_REGEX_STR)
const val NICKNAME_REGEX_DESC = "닉네임은 $NICKNAME_MIN_LENGTH 자 이상 $NICKNAME_MAX_LENGTH 자 이하의 한글, 영문, 숫자 여야 합니다."
