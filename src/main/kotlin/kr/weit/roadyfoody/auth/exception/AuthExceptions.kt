package kr.weit.roadyfoody.auth.exception

import kr.weit.roadyfoody.common.exception.BaseException
import kr.weit.roadyfoody.common.exception.ErrorCode

class InvalidTokenException : BaseException(
    ErrorCode.INVALID_TOKEN,
    ErrorCode.INVALID_TOKEN.errorMessage,
)

class UserAlreadyExistsException : BaseException(
    ErrorCode.USER_ALREADY_EXISTS,
    ErrorCode.USER_ALREADY_EXISTS.errorMessage,
)

class MissingSocialAccessTokenException() : BaseException(
    ErrorCode.MISSING_SOCIAL_ACCESS_TOKEN,
    ErrorCode.MISSING_SOCIAL_ACCESS_TOKEN.errorMessage,
)

class UserNotRegisteredException() : BaseException(
    ErrorCode.USER_NOT_REGISTERED,
    ErrorCode.USER_NOT_REGISTERED.errorMessage,
)

class MissingRefreshTokenException() : BaseException(
    ErrorCode.MISSING_REFRESH_TOKEN,
    ErrorCode.MISSING_REFRESH_TOKEN.errorMessage,
)

class InvalidRefreshTokenException() : BaseException(
    ErrorCode.INVALID_REFRESH_TOKEN,
    ErrorCode.INVALID_REFRESH_TOKEN.errorMessage,
)

class AuthenticatedUserNotFoundException() :
    BaseException(
        ErrorCode.AUTHENTICATED_USER_NOT_FOUND,
        ErrorCode.AUTHENTICATED_USER_NOT_FOUND.errorMessage,
    )
