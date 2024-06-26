package kr.weit.roadyfoody.auth.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class UserAlreadyExistsException : BaseException(
    ErrorCode.EXIST_RESOURCE,
    "이미 존재하는 유저입니다.",
)
