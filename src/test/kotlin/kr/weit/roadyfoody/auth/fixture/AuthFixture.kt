package kr.weit.roadyfoody.auth.fixture

import kr.weit.roadyfoody.auth.domain.SocialAccessToken
import kr.weit.roadyfoody.auth.dto.KakaoUserResponse
import kr.weit.roadyfoody.auth.dto.SignUpRequest
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.support.utils.generateImageBytes
import kr.weit.roadyfoody.term.fixture.createTestRequiredTermIdSet
import kr.weit.roadyfoody.user.fixture.TEST_SOCIAL_ID
import kr.weit.roadyfoody.user.fixture.TEST_SOCIAL_LOGIN_TYPE
import kr.weit.roadyfoody.user.fixture.TEST_USER_NICKNAME
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

val TEST_SOCIAL_ACCESS_TOKEN = SocialAccessToken("test-token")
val TEST_CONNECTED_AT: LocalDateTime = LocalDateTime.now()

fun createTestKakaoUserResponse(): KakaoUserResponse =
    KakaoUserResponse(
        TEST_SOCIAL_ID,
        TEST_CONNECTED_AT,
    )

fun createTestSignUpRequest(
    termIdSet: Set<Long> = createTestRequiredTermIdSet(),
    profileImage: MultipartFile? = createTestImageFile(ImageFormat.WEBP),
): SignUpRequest =
    SignUpRequest(
        TEST_USER_NICKNAME,
        profileImage,
        termIdSet,
        TEST_SOCIAL_LOGIN_TYPE,
    )

fun createTestImageFile(format: ImageFormat): MultipartFile =
    MockMultipartFile(
        "mockImage${format.values}",
        "mockImage${format.values}.$format.value",
        "image/$format.value",
        generateImageBytes(format),
    )

const val TEST_BEARER_TOKEN = "Bearer test-token"
