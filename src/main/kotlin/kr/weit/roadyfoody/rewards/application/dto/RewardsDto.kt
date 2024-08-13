package kr.weit.roadyfoody.rewards.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.rewards.domain.Rewards
import java.time.LocalDateTime


data class RewardsResponse (
    @Schema(description = "리워드 ID")
    val id : Long,
    @Schema(description = "리워드 종류")
    val rewardType : String,
    @Schema(description = "리워드 코인")
    val rewardPoint : Int,
    @Schema(description = "리워드 이유")
    val rewardReason : String,
    @Schema(description = "Rewards 생성일")
    val createdAt : LocalDateTime
) {
    constructor(rewards: Rewards) : this (
        id = rewards.id,
        rewardType = rewards.rewardType.toString(),
        rewardPoint = rewards.rewardPoint,
        rewardReason = rewards.rewardReason.toString(),
        createdAt = rewards.createdDateTime
    )
}