package kr.weit.roadyfoody.term.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class TermNotFoundException(
    message: String,
) : BaseException(ErrorCode.NO_SUCH_ELEMENT, message) {
    companion object {
        private const val TERM_NOT_FOUND_MESSAGE_PREFIX = "약관 ID:"
        private const val TERM_NOT_FOUND_MESSAGE_SUFFIX = "약관을 찾을 수 없습니다."

        @JvmStatic
        fun termNotFoundMessage(termId: Long) = "$TERM_NOT_FOUND_MESSAGE_PREFIX $termId $TERM_NOT_FOUND_MESSAGE_SUFFIX"
    }
}
