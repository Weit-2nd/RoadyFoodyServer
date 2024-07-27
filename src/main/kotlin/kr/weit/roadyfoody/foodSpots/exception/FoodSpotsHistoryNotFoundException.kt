package kr.weit.roadyfoody.foodSpots.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class FoodSpotsHistoryNotFoundException :
    BaseException(
        ErrorCode.NOT_FOUND_REPORT_HISTORY,
        ErrorCode.NOT_FOUND_REPORT_HISTORY.errorMessage,
    )
