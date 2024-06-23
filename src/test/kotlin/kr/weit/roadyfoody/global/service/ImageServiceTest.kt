package kr.weit.roadyfoody.global.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.auth.fixture.TEST_WEBP_EXTENSION
import kr.weit.roadyfoody.auth.fixture.TEST_WEBP_IMAGE
import kr.weit.roadyfoody.user.fixture.TEST_USER_PROFILE_IMAGE_NAME
import org.springframework.util.StringUtils
import java.io.InputStream

class ImageServiceTest : BehaviorSpec({
    val storageService = mockk<StorageService>()
    val imageService = ImageService(storageService)

    given("upload 메소드") {
        `when`("이미지를 업로드 하면") {
            every { storageService.upload(TEST_USER_PROFILE_IMAGE_NAME, any<InputStream>()) } returns ""
            then("이미지가 저장된다.") {
                imageService.upload(TEST_USER_PROFILE_IMAGE_NAME, TEST_WEBP_IMAGE)
                verify(exactly = 1) { storageService.upload(TEST_USER_PROFILE_IMAGE_NAME, any<InputStream>()) }
            }
        }
    }

    given("generateImageName 메소드") {
        `when`("확장자가 있는 이미지 이름을 전달하면") {
            then("확장자가 있는 이미지 이름이 생성된다.") {
                val imageName = imageService.generateImageName(TEST_WEBP_IMAGE.originalFilename)
                StringUtils.getFilenameExtension(imageName).shouldNotBeNull()
            }
        }

        `when`("확장자가 없는 이미지 이름을 전달하면") {
            then("확장자가 없는 이미지 이름이 생성된다.") {
                val imageName = imageService.generateImageName(TEST_WEBP_IMAGE.originalFilename.removeSuffix(TEST_WEBP_EXTENSION))
                StringUtils.getFilenameExtension(imageName) shouldBe "null"
            }
        }
    }
})
