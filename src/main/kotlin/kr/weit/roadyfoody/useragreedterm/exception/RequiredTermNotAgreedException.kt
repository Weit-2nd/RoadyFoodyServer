package kr.weit.roadyfoody.useragreedterm.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class RequiredTermNotAgreedException(
    termIds: Iterable<Long>,
) : BaseException(
        ErrorCode.INVALID_REQUEST,
        "$ERROR_MSG_PREFIX ${termIds.joinToString()} $ERROR_MSG_SUFFIX",
    )

private const val ERROR_MSG_PREFIX = "약관 ID: "
private const val ERROR_MSG_SUFFIX = "에 동의하지 않았습니다."
