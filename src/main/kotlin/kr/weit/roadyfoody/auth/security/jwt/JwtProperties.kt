package kr.weit.roadyfoody.auth.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
class JwtProperties(
    val accessKey: String,
    val refreshKey: String,
    val accessTokenExpirationTime: Long,
    val refreshTokenExpirationTime: Long,
)
