package kr.weit.roadyfoody.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val code: Int,
    val errorMessage: String,
) {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, -10000, "Invalid request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, -10001, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, -10002, "Forbidden"),
    NO_SUCH_ELEMENT(HttpStatus.NOT_FOUND, -10003, "No such element"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -10004, "Internal server error"),
    EXIST_RESOURCE(HttpStatus.CONFLICT, -10005, "Exist resource"),
    NOT_FOUND_DEFAULT_RESOURCE(HttpStatus.INTERNAL_SERVER_ERROR, -10007, "Not found default resource"),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, -10008, "Payload too large"),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, -10009, "Not found user"),
    NOT_FOUND_TERM(HttpStatus.NOT_FOUND, -10010, "Not found term"),
    NOT_FOUND_FOOD_CATEGORY(HttpStatus.NOT_FOUND, -10011, "해당 카테고리가 존재하지 않습니다."),

    // Bad Request -10000으로 코드 통일
    SIZE_NON_POSITIVE(HttpStatus.BAD_REQUEST, -10000, "조회할 개수는 양수여야 합니다."),
    LAST_ID_NON_POSITIVE(HttpStatus.BAD_REQUEST, -10000, "마지막 ID는 양수여야 합니다."),
    INVALID_LENGTH_FOOD_SPOTS_NAME(HttpStatus.BAD_REQUEST, -10000, "상호명은 1자 이상 20자 이하 한글, 영문, 숫자, 특수문자 여야 합니다."),
    INVALID_CHARACTERS_FOOD_SPOTS_NAME(HttpStatus.BAD_REQUEST, -10000, "설명은 1자 이상 100자 이하로 입력해주세요."),
    LATITUDE_TOO_HIGH(HttpStatus.BAD_REQUEST, -10000, "경도는 180 이하여야 합니다."),
    LATITUDE_TOO_LOW(HttpStatus.BAD_REQUEST, -10000, "경도는 -180 이상이어야 합니다."),
    LONGITUDE_TOO_HIGH(HttpStatus.BAD_REQUEST, -10000, "위도는 180 이하여야 합니다."),
    LONGITUDE_TOO_LOW(HttpStatus.BAD_REQUEST, -10000, "위도는 -180 이상이어야 합니다."),
    NO_CATEGORY_SELECTED(HttpStatus.BAD_REQUEST, -10000, "음식 카테고리는 최소 1개 이상 선택해야 합니다."),
    INVALID_FORMAT_OPERATION_HOURS(HttpStatus.BAD_REQUEST, -10000, "시간은 00:00부터 23:59까지의 형식이어야 합니다."),
    IMAGES_TOO_MANY(HttpStatus.BAD_REQUEST, -10000, "이미지는 최대 3개까지 업로드할 수 있습니다."),
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, -10000, "하나 이상의 파일이 잘못 되었습니다."),
    IMAGES_SIZE_TOO_LARGE(HttpStatus.BAD_REQUEST, -10000, "하나 이상의 파일이 잘못 되었습니다."),

    // Search API error 11000대
    REST_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -11000, "외부 API 호출 중 에러 발생"),
    RETRIES_EXCEEDED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -11001, "외부 API 호출 재시도 횟수 초과"),
    SEARCH_KEYWORD_LENGTH(HttpStatus.BAD_REQUEST, -10000, "검색어는 2자 이상 60자 이하로 입력해주세요."),
}
