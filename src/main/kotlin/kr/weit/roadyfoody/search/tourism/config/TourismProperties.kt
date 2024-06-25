package kr.weit.roadyfoody.search.tourism.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "client.tour")
class TourismProperties(
    val apiKey: String,
    val mobileApp: String,
    val mobileOs: String,
)
