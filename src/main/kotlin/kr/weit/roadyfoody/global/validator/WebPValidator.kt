package kr.weit.roadyfoody.global.validator

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.apache.tika.Tika
import org.springframework.web.multipart.MultipartFile
import kotlin.reflect.KClass

@Constraint(validatedBy = [WebPImageValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebPImage(
    val message: String = "webp 형식이 아닙니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class WebPImageValidator : ConstraintValidator<WebPImage, MultipartFile?> {
    override fun isValid(
        p0: MultipartFile?,
        p1: ConstraintValidatorContext?,
    ): Boolean = p0 == null || Tika().detect(p0.inputStream) == "image/webp"
}
