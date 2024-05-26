package kr.weit.roadyfoody.dto

import kr.weit.roadyfoody.support.exception.ErrorCode

open class ErrorResponse private constructor(val code: Int, val errorMessage: String) {
    protected constructor(errorCode: ErrorCode) : this(errorCode.code, errorCode.errorMessage)

    companion object {
        fun of(
            errorCode: ErrorCode,
            errorMessage: String?,
        ) = ErrorResponse(errorCode.code, errorMessage ?: errorCode.errorMessage)
    }
}
