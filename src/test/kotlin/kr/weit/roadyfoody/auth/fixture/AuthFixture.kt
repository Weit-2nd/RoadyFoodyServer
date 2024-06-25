package kr.weit.roadyfoody.auth.fixture

import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.support.utils.createMultipartFile
import kr.weit.roadyfoody.support.utils.generateImageBytes
import org.springframework.mock.web.MockMultipartFile

const val TEST_IMAGE_FILE_NAME = "testImage"

fun createTestImageFile(
    format: ImageFormat,
    name: String = TEST_IMAGE_FILE_NAME,
): MockMultipartFile = createMultipartFile(name, generateImageBytes(format).inputStream(), "image/${format.first()}")

const val PROFILE_IMAGE_FILE_NAME = "profileImage"
const val SIGN_UP_REQUEST_FILE_NAME = "signUpRequest"

const val TEST_BEARER_TOKEN = "Bearer test-token"
