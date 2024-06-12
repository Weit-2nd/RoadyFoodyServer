package kr.weit.roadyfoody.common.exception

open class BaseException(val errorCode: ErrorCode, message: String) : RuntimeException(message)

class UserNotFoundException(message: String) : BaseException(ErrorCode.NO_SUCH_ELEMENT, message)
