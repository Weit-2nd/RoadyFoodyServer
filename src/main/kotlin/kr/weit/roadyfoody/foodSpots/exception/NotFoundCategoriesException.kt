package kr.weit.roadyfoody.foodSpots.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class NotFoundCategoriesException : BaseException(ErrorCode.NOT_FOUND_FOOD_CATEGORY, "선택하신 카테고리가 전부 존재하지 않습니다.")
