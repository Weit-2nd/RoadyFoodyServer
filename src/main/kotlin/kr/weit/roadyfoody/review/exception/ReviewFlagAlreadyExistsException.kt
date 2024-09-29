package kr.weit.roadyfoody.review.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class ReviewFlagAlreadyExistsException(
    message: String = ErrorCode.REVIEW_FLAG_ALREADY_EXISTS.errorMessage,
) : BaseException(ErrorCode.REVIEW_FLAG_ALREADY_EXISTS, message)
