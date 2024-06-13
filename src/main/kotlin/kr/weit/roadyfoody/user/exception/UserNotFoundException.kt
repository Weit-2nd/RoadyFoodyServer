package kr.weit.roadyfoody.user.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class UserNotFoundException(message: String) : BaseException(ErrorCode.NO_SUCH_ELEMENT, message)
