package kr.weit.roadyfoody.foodSpots.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class UnauthorizedPhotoRemoveException(
    message: String = ErrorCode.UNAUTHORIZED_PHOTO_REMOVE.errorMessage,
) : BaseException(ErrorCode.UNAUTHORIZED_PHOTO_REMOVE, message)
