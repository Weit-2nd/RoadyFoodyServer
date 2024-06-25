package kr.weit.roadyfoody.global.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component

@Component
class ObjectMapperProvider {
    val objectMapper = jacksonObjectMapper()
}
