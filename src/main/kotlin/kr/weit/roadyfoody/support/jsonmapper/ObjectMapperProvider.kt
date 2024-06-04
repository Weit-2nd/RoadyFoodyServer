package kr.weit.roadyfoody.support.jsonmapper

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component

@Component
class ObjectMapperProvider {
    val objectMapper = jacksonObjectMapper()
}
