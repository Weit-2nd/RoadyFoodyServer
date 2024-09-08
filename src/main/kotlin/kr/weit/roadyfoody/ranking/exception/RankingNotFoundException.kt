package kr.weit.roadyfoody.ranking.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class RankingNotFoundException(
    message: String = ErrorCode.NOT_FOUND_FOOD_SPOTS_RANKING.errorMessage,
) : BaseException(ErrorCode.NOT_FOUND_FOOD_SPOTS_RANKING, message)
