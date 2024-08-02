package kr.weit.roadyfoody.review.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class ReviewNotFoundException :
    BaseException(
        ErrorCode.NOT_FOUND_FOOD_SPOTS_REVIEW,
        ErrorCode.NOT_FOUND_FOOD_SPOTS_REVIEW.errorMessage,
    )
