package kr.weit.roadyfoody.common.exception

class RestClientException(
    message: String? = ErrorCode.REST_CLIENT_ERROR.errorMessage,
) : BaseException(ErrorCode.REST_CLIENT_ERROR, message)
