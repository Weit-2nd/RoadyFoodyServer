package kr.weit.roadyfoody.auth.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class KakaoUserResponse(
    val id: Long,
    @JsonProperty("connected_at")
    val connectedAt: LocalDateTime,
)
