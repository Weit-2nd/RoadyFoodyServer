package kr.weit.roadyfoody.support.utils

import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import java.io.InputStream

fun createMultipartFile(
    name: String,
    contentStream: InputStream,
    contentType: String = MediaType.APPLICATION_JSON_VALUE,
): MockMultipartFile = MockMultipartFile(name, name, contentType, contentStream)

const val TEST_IMAGE_FILE_NAME = "testImage"

fun createTestImageFile(
    format: ImageFormat,
    name: String = TEST_IMAGE_FILE_NAME,
): MockMultipartFile = createMultipartFile(name, generateImageBytes(format).inputStream(), "image/${format.first()}")
