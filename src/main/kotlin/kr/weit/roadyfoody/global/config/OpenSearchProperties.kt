package kr.weit.roadyfoody.global.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.opensearch")
class OpenSearchProperties(
    val username: String,
    val password: String,
    val uri: String,
    val port: Int,
    val scheme: String,
)
