package kr.weit.roadyfoody.common.exception

open class BaseException(val errorCode: ErrorCode, message: String?) : RuntimeException(message)
