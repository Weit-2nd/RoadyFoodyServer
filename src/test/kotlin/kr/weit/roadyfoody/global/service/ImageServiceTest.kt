package kr.weit.roadyfoody.global.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.shouldForAny
import io.kotest.matchers.string.shouldEndWith
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.auth.fixture.createTestImageFile
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.user.fixture.TEST_USER_PROFILE_IMAGE_NAME
import java.io.InputStream

class ImageServiceTest : BehaviorSpec({
    val storageService = mockk<StorageService>()
    val imageService = ImageService(storageService)

    given("upload 메소드") {
        `when`("이미지를 업로드 하면") {
            every { storageService.upload(TEST_USER_PROFILE_IMAGE_NAME, any<InputStream>()) } returns ""
            then("이미지가 저장된다.") {
                imageService.upload(TEST_USER_PROFILE_IMAGE_NAME, createTestImageFile(ImageFormat.WEBP))
                verify(exactly = 1) { storageService.upload(TEST_USER_PROFILE_IMAGE_NAME, any<InputStream>()) }
            }
        }
    }

    given("generateImageName 메소드") {
        `when`("${ImageFormat.WEBP.getStrValues()} 이미지 파일을 전달하면") {
            then("${ImageFormat.WEBP.getStrValues()} 확장자가 있는 이름이 생성된다.") {
                val imageName = imageService.generateImageName(createTestImageFile(ImageFormat.WEBP))
                ImageFormat.WEBP.values.shouldForAny { imageName.shouldEndWith(".$it") }
            }
        }

        `when`("${ImageFormat.JPEG.getStrValues()} 이미지 파일을 전달하면") {
            then("${ImageFormat.JPEG.getStrValues()} 확장자가 있는 이름이 생성된다.") {
                val imageName = imageService.generateImageName(createTestImageFile(ImageFormat.JPEG))
                ImageFormat.JPEG.values.shouldForAny { imageName.shouldEndWith(".$it") }
            }
        }

        `when`("${ImageFormat.PNG.getStrValues()} 이미지 파일을 전달하면") {
            then("${ImageFormat.PNG.getStrValues()} 확장자가 있는 이름이 생성된다.") {
                val imageName = imageService.generateImageName(createTestImageFile(ImageFormat.PNG))
                ImageFormat.PNG.values.shouldForAny { imageName.shouldEndWith(".$it") }
            }
        }

        `when`("${ImageFormat.GIF.getStrValues()} 이미지 파일을 전달하면") {
            then("${ImageFormat.GIF.getStrValues()} 확장자가 있는 이름이 생성된다.") {
                val imageName = imageService.generateImageName(createTestImageFile(ImageFormat.GIF))
                ImageFormat.GIF.values.shouldForAny { imageName.shouldEndWith(".$it") }
            }
        }
    }
})
