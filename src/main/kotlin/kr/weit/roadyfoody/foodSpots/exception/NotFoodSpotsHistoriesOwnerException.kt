package kr.weit.roadyfoody.foodSpots.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class NotFoodSpotsHistoriesOwnerException(
    message: String,
) : BaseException(ErrorCode.NOT_FOOD_SPOTS_HISTORIES_OWNER, message)
