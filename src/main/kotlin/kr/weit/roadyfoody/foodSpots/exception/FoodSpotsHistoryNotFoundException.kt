package kr.weit.roadyfoody.foodSpots.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class FoodSpotsHistoryNotFoundException(
    message: String,
) : BaseException(ErrorCode.NOT_FOUND_FOOD_SPOTS_HISTORIES, message)
