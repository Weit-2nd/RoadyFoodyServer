package kr.weit.roadyfoody.support.utils

import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import java.io.InputStream

fun createMultipartFile(
    name: String,
    contentStream: InputStream,
    contentType: String = MediaType.APPLICATION_JSON_VALUE,
): MockMultipartFile = MockMultipartFile(name, name, contentType, contentStream)
