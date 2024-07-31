package kr.weit.roadyfoody.review.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class NotFoodSpotsReviewOwnerException(
    message: String,
) : BaseException(ErrorCode.NOT_FOOD_SPOTS_REVIEW_OWNER, message)
