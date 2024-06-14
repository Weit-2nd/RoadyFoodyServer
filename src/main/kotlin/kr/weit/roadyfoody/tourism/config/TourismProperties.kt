package kr.weit.roadyfoody.tourism.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "client.tour")
class TourismProperties(
    val apiKey: String,
    val mobileApp: String,
    val mobileOs: String,
    val contentTypeId: String,
)
