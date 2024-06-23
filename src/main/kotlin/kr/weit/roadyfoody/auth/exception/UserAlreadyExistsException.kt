package kr.weit.roadyfoody.auth.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class UserAlreadyExistsException : BaseException(
    ErrorCode.INVALID_REQUEST,
    "이미 존재하는 유저입니다.",
)
