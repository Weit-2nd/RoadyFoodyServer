package kr.weit.roadyfoody.support.jsonmapper

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ObjectMapperProvider {
    val objectMapper = jacksonObjectMapper()
}
