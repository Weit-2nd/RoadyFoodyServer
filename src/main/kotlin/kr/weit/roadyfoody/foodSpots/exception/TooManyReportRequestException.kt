package kr.weit.roadyfoody.foodSpots.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class TooManyReportRequestException(
    message: String = ErrorCode.TOO_MANY_REQUESTS.errorMessage,
) : BaseException(ErrorCode.TOO_MANY_REQUESTS, message)
