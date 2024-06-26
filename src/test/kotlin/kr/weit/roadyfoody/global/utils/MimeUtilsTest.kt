package kr.weit.roadyfoody.global.utils

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeIn
import kr.weit.roadyfoody.support.utils.ImageFormat.GIF
import kr.weit.roadyfoody.support.utils.ImageFormat.JPEG
import kr.weit.roadyfoody.support.utils.ImageFormat.PNG
import kr.weit.roadyfoody.support.utils.ImageFormat.WEBP
import kr.weit.roadyfoody.support.utils.createTestImageFile

class MimeUtilsTest :
    BehaviorSpec({
        given("detectMimeType 메소드") {
            `when`("WEBP 이미지 파일을 전달하면") {
                then("WEBP 이미지 MIME 타입을 반환한다.") {
                    val imageFile = createTestImageFile(WEBP)
                    val mimeType = MimeUtils.detectMimeType(imageFile)
                    mimeType shouldBeIn WEBP.getContentTypes()
                }
            }

            `when`("JPEG 이미지 파일을 전달하면") {
                then("JPEG 이미지 MIME 타입을 반환한다.") {
                    val imageFile = createTestImageFile(JPEG)
                    val mimeType = MimeUtils.detectMimeType(imageFile)
                    mimeType shouldBeIn JPEG.getContentTypes()
                }
            }

            `when`("PNG 이미지 파일을 전달하면") {
                then("PNG 이미지 MIME 타입을 반환한다.") {
                    val imageFile = createTestImageFile(PNG)
                    val mimeType = MimeUtils.detectMimeType(imageFile)
                    mimeType shouldBeIn PNG.getContentTypes()
                }
            }

            `when`("GIF 이미지 파일을 전달하면") {
                then("GIF 이미지 MIME 타입을 반환한다.") {
                    val imageFile = createTestImageFile(GIF)
                    val mimeType = MimeUtils.detectMimeType(imageFile)
                    mimeType shouldBeIn GIF.getContentTypes()
                }
            }
        }

        given("getFileExtension 메소드") {
            `when`("WEBP 이미지 파일을 전달하면") {
                then("WEBP 이미지 확장자를 반환한다.") {
                    val imageFile = createTestImageFile(WEBP)
                    val extension = MimeUtils.getFileExtension(imageFile)
                    extension shouldBeIn WEBP.values.map { ".$it" }
                }
            }

            `when`("JPEG 이미지 파일을 전달하면") {
                then("JPEG 이미지 확장자를 반환한다.") {
                    val imageFile = createTestImageFile(JPEG)
                    val extension = MimeUtils.getFileExtension(imageFile)
                    extension shouldBeIn JPEG.values.map { ".$it" }
                }
            }

            `when`("PNG 이미지 파일을 전달하면") {
                then("PNG 이미지 확장자를 반환한다.") {
                    val imageFile = createTestImageFile(PNG)
                    val extension = MimeUtils.getFileExtension(imageFile)
                    extension shouldBeIn PNG.values.map { ".$it" }
                }
            }

            `when`("GIF 이미지 파일을 전달하면") {
                then("GIF 이미지 확장자를 반환한다.") {
                    val imageFile = createTestImageFile(GIF)
                    val extension = MimeUtils.getFileExtension(imageFile)
                    extension shouldBeIn GIF.values.map { ".$it" }
                }
            }
        }
    })
