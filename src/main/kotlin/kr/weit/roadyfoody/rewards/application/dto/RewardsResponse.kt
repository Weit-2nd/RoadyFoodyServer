package kr.weit.roadyfoody.rewards.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.rewards.domain.Rewards
import java.time.LocalDateTime

data class RewardsResponse(
    @Schema(description = "리워드 ID")
    val id: Long,
    @Schema(description = "코인 획득/소모 여부")
    val coinReceived: Boolean,
    @Schema(description = "리워드 코인")
    val rewardPoint: Int,
    @Schema(description = "리워드 종류")
    val rewardType: String,
    @Schema(description = "Rewards 생성일")
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(rewards: Rewards): RewardsResponse =
            RewardsResponse(
                rewards.id,
                rewards.coinReceived,
                rewards.rewardPoint,
                rewards.rewardType.toString(),
                rewards.createdDateTime,
            )
    }
}
