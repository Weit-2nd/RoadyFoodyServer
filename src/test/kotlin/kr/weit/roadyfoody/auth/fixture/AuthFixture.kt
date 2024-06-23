package kr.weit.roadyfoody.auth.fixture

import kr.weit.roadyfoody.auth.domain.SocialAccessToken
import kr.weit.roadyfoody.auth.dto.KakaoUserResponse
import kr.weit.roadyfoody.auth.dto.SignUpRequest
import kr.weit.roadyfoody.term.fixture.createTestRequiredTermIdSet
import kr.weit.roadyfoody.user.fixture.TEST_SOCIAL_ID
import kr.weit.roadyfoody.user.fixture.TEST_SOCIAL_LOGIN_TYPE
import kr.weit.roadyfoody.user.fixture.TEST_USER_NICKNAME
import org.springframework.core.io.ClassPathResource
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
    profileImage: MultipartFile? = TEST_WEBP_IMAGE,
): SignUpRequest =
    SignUpRequest(
        TEST_USER_NICKNAME,
        profileImage,
        termIdSet,
        TEST_SOCIAL_LOGIN_TYPE,
    )

val TEST_WEBP_IMAGE_BYTES = ClassPathResource("image/webp_test_img.webp").inputStream.readBytes()
val TEST_WEBP_IMAGE = MockMultipartFile("mockImage", "mockImage.webp", "image/webp", TEST_WEBP_IMAGE_BYTES)
const val TEST_WEBP_EXTENSION = ".webp"

const val TEST_BEARER_TOKEN = "Bearer test-token"

// fail case

// 확장자만 webp 이고 내용은 gif
val TEST_FAKE_WEBP_IMAGE_BYTES = ClassPathResource("image/fake_webp_test_img.webp").inputStream.readBytes()

val TEST_JPEG_IMAGE_BYTES = ClassPathResource("image/jpeg_test_img.jpeg").inputStream.readBytes()

val TEST_PNG_IMAGE_BYTES = ClassPathResource("image/png_test_img.png").inputStream.readBytes()

val TEST_GIF_IMAGE_BYTES = ClassPathResource("image/gif_test_img.gif").inputStream.readBytes()
