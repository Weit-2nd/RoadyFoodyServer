package kr.weit.roadyfoody.term.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class TermNotFoundException(
    termId: Long,
) : BaseException(
        ErrorCode.NOT_FOUND_TERM,
        "$ERROR_MSG_PREFIX $termId $ERROR_MSG_SUFFIX",
    )

const val ERROR_MSG_PREFIX = "약관 ID:"
const val ERROR_MSG_SUFFIX = "약관을 찾을 수 없습니다."
