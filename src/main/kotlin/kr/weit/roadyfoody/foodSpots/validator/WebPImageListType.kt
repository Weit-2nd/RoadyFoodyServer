package kr.weit.roadyfoody.foodSpots.validator

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kr.weit.roadyfoody.global.validator.MaxFileSize
import kr.weit.roadyfoody.global.validator.MaxFileSizeValidator
import kr.weit.roadyfoody.global.validator.WebPImageValidator
import org.springframework.web.multipart.MultipartFile
import kotlin.reflect.KClass

@Constraint(validatedBy = [WebPImageListValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebPImageList(
    val message: String = "하나 이상의 파일이 잘못 되었습니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class WebPImageListValidator : ConstraintValidator<WebPImageList, List<MultipartFile>> {
    private val webPImageValidator = WebPImageValidator()
    private var maxFileSizeValidator = MaxFileSizeValidator()

    override fun initialize(constraintAnnotation: WebPImageList) {
        maxFileSizeValidator.initialize(MaxFileSize())
    }

    override fun isValid(
        files: List<MultipartFile>?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (files == null) return true

        for (file in files) {
            if (!(maxFileSizeValidator.isValid(file, context) && webPImageValidator.isValid(file, context))) {
                return false
            }
        }
        return true
    }
}
