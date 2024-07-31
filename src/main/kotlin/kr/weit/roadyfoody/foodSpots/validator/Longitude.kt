package kr.weit.roadyfoody.foodSpots.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import kotlin.reflect.KClass

@Min(-180, message = "경도는 -180 이상이어야 합니다.")
@Max(180, message = "경도는 180 이하여야 합니다.")
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
annotation class Longitude(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
