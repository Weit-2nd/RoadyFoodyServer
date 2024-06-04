package kr.weit.roadyfoody.support.regex

const val NICKNAME_REGEX_STR = "^[A-Za-z0-9가-힣]{6,16}$"
val NICKNAME_REGEX = Regex(NICKNAME_REGEX_STR)
const val NICKNAME_REGEX_DESC = "닉네임은 6자 이상 16자 이하의 한글, 영문, 숫자 여야 합니다."
