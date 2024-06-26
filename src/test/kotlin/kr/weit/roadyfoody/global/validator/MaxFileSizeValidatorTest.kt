package kr.weit.roadyfoody.global.validator

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import org.springframework.web.multipart.MultipartFile

class MaxFileSizeValidatorTest : BehaviorSpec({
    val maxFileSizeValidator = MaxFileSizeValidator()
    val maxFileSize = 1 * 1024 * 1024 // 1MB

    beforeTest {
        maxFileSizeValidator.initialize(MaxFileSize(max = maxFileSize))
    }

    given("MaxFileSizeValidator 테스트") {
        val mockFile = mockk<MultipartFile>()

        `when`("파일 크기가 1MB 이하이면") {
            then("MaxFileSizeValidator 를 통과한다.") {
                every { mockFile.size } returns maxFileSize.toLong()
                val actual = maxFileSizeValidator.isValid(mockFile, mockk())
                actual.shouldBeTrue()
            }
        }

        `when`("파일 크기가 1MB 보다 크면") {
            then("MaxFileSizeValidator 를 통과하지 못한다.") {
                every { mockFile.size } returns (maxFileSize + 1).toLong()
                val actual = maxFileSizeValidator.isValid(mockFile, mockk())
                actual.shouldBeFalse()
            }
        }

        `when`("파일이 null 이면") {
            then("MaxFileSizeValidator 를 통과한다.") {
                val actual = maxFileSizeValidator.isValid(null, mockk())
                actual.shouldBeTrue()
            }
        }
    }
})
