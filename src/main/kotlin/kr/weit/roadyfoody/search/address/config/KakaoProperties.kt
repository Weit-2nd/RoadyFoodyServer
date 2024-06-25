package kr.weit.roadyfoody.search.address.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "client.kakao")
class KakaoProperties(
    val apiKey: String,
)
