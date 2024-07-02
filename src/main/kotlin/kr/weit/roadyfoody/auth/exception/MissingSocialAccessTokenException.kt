package kr.weit.roadyfoody.auth.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class MissingSocialAccessTokenException() : BaseException(
    ErrorCode.UNAUTHORIZED,
    "SocialAccessToken 이 존재하지 않습니다.",
)
