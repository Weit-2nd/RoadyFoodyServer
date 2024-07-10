package kr.weit.roadyfoody.foodSpots.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class CategoriesNotFoundException : BaseException(ErrorCode.NOT_FOUND_FOOD_CATEGORY, ErrorCode.NOT_FOUND_FOOD_CATEGORY.errorMessage)
