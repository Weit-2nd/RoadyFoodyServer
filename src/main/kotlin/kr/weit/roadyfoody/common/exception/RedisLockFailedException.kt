package kr.weit.roadyfoody.common.exception

class RedisLockFailedException(
    message: String? = ErrorCode.REDISSON_LOCK_TOO_MANY_REQUEST.errorMessage,
) : BaseException(ErrorCode.REDISSON_LOCK_TOO_MANY_REQUEST, message)
