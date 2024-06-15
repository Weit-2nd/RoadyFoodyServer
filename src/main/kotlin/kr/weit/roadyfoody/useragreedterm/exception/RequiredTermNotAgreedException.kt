package kr.weit.roadyfoody.useragreedterm.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class RequiredTermNotAgreedException(
    message: String,
) : BaseException(ErrorCode.INVALID_REQUEST, message) {
    companion object {
        private const val REQUIRED_TERM_NOT_AGREED_MESSAGE_PREFIX = "약관 ID:"
        private const val REQUIRED_TERM_NOT_AGREED_MESSAGE_SUFFIX = "필수 약관에 동의하지 않았습니다."

        @JvmStatic
        fun getRequiredTermNotAgreedMessage(termIds: Iterable<Long>) =
            "$REQUIRED_TERM_NOT_AGREED_MESSAGE_PREFIX ${termIds.joinToString()} $REQUIRED_TERM_NOT_AGREED_MESSAGE_SUFFIX"
    }
}
