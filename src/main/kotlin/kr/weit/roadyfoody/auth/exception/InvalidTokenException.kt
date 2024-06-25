package kr.weit.roadyfoody.auth.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class InvalidTokenException : BaseException(
    ErrorCode.UNAUTHORIZED,
    "유효하지 않은 토큰입니다.",
)
