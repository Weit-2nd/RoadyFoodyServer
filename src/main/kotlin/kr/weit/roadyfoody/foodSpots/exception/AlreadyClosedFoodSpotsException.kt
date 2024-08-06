package kr.weit.roadyfoody.foodSpots.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class AlreadyClosedFoodSpotsException(
    override val message: String = ErrorCode.FOOD_SPOTS_ALREADY_CLOSED.errorMessage,
) : BaseException(ErrorCode.FOOD_SPOTS_ALREADY_CLOSED, message)
