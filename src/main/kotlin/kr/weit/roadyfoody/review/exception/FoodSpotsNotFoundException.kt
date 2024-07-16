package kr.weit.roadyfoody.review.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class FoodSpotsNotFoundException : BaseException(ErrorCode.NOT_FOUND_FOOD_SPOTS, ErrorCode.NOT_FOUND_FOOD_SPOTS.errorMessage)
