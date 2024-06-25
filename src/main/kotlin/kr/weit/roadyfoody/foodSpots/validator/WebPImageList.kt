package kr.weit.roadyfoody.foodSpots.validator

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.apache.tika.Tika
import org.springframework.web.multipart.MultipartFile
import kotlin.reflect.KClass

@Constraint(validatedBy = [WebPImageListValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebPImageList(
    val message: String = "하나 이상의 파일이 webp 형식이 아닙니다.",
    val maxFileSize: Long = 1 * 1024 * 1024,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class WebPImageListValidator : ConstraintValidator<WebPImageList, List<MultipartFile>?> {
    private val tika = Tika()

    private var maxFileSize: Long = 0

    override fun initialize(constraintAnnotation: WebPImageList) {
        maxFileSize = constraintAnnotation.maxFileSize
    }

    override fun isValid(
        files: List<MultipartFile>?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        if (files == null) return true

        for (file in files) {
            if (tika.detect(file.inputStream) != "image/webp" || file.size > maxFileSize) {
                return false
            }
        }
        return true
    }
}
