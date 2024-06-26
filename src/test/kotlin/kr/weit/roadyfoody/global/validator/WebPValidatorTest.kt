package kr.weit.roadyfoody.global.validator

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kr.weit.roadyfoody.support.utils.ImageFormat.GIF
import kr.weit.roadyfoody.support.utils.ImageFormat.JPEG
import kr.weit.roadyfoody.support.utils.ImageFormat.PNG
import kr.weit.roadyfoody.support.utils.ImageFormat.WEBP
import kr.weit.roadyfoody.support.utils.createTestImageFile

class WebPValidatorTest(
    private val webPValidator: WebPImageValidator = WebPImageValidator(),
) : BehaviorSpec({
        given("WebPValidator 테스트") {
            `when`("WebP 이미지 파일을 전달받으면") {
                then("WebPValidator 를 통과한다.") {
                    val actual = webPValidator.isValid(createTestImageFile(WEBP), null)
                    actual.shouldBeTrue()
                }
            }

            `when`("WebP 이미지 파일이 아닌 파일을 전달받으면") {
                then("WebPValidator 를 통과하지 못한다.") {
                    forAll(
                        row(JPEG),
                        row(PNG),
                        row(GIF),
                    ) {
                        val actual = webPValidator.isValid(createTestImageFile(it), null)
                        actual.shouldBeFalse()
                    }
                }
            }
        }
    })
