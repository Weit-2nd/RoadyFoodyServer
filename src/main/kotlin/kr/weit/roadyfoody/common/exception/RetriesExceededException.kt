package kr.weit.roadyfoody.common.exception

class RetriesExceededException(
    message: String? = ErrorCode.RETRIES_EXCEEDED_ERROR.errorMessage,
) : BaseException(ErrorCode.RETRIES_EXCEEDED_ERROR, message)
