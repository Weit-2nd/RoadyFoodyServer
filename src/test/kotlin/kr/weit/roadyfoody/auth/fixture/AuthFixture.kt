package kr.weit.roadyfoody.auth.fixture

import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.support.utils.generateImageBytes
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

const val TEST_IMAGE_FILE_NAME = "testImage"

fun createTestImageFile(
    format: ImageFormat,
    name: String = TEST_IMAGE_FILE_NAME,
    originalName: String = TEST_IMAGE_FILE_NAME,
): MultipartFile =
    MockMultipartFile(
        name,
        name,
        "image/$format.value",
        generateImageBytes(format),
    )

const val PROFILE_IMAGE_FILE_NAME = "profileImage"

fun createProfileImageFile(
    format: ImageFormat,
    name: String = PROFILE_IMAGE_FILE_NAME,
    originalName: String = PROFILE_IMAGE_FILE_NAME,
): MockMultipartFile =
    MockMultipartFile(
        name,
        originalName,
        "image/$format.value",
        generateImageBytes(format),
    )

const val SIGN_UP_REQUEST_FILE_NAME = "signUpRequest"

fun createSignUpRequestFile(
    contentStream: InputStream,
    name: String = SIGN_UP_REQUEST_FILE_NAME,
    originalName: String = SIGN_UP_REQUEST_FILE_NAME,
    contentType: String = MediaType.APPLICATION_JSON_VALUE,
): MockMultipartFile = MockMultipartFile(name, originalName, contentType, contentStream)

const val TEST_BEARER_TOKEN = "Bearer test-token"
