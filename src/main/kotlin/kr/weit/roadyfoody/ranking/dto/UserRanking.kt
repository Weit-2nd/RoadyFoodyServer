package kr.weit.roadyfoody.ranking.dto

import io.swagger.v3.oas.annotations.media.Schema

data class UserRanking(
    @Schema(description = "유저 닉네임", example = "로디푸디유저")
    val userNickname: String,
    @Schema(description = "총합수", example = "1")
    val total: Long,
    @Schema(description = "유저아이디", example = "1")
    val userId: Long,
    @Schema(description = "프로필 URL")
    val profileImageUrl: String?,
)

data class UserRankingResponse(
    @Schema(description = "순위", example = "1")
    val ranking: Long,
    @Schema(description = "유저 닉네임", example = "로디푸디유저")
    val userNickname: String,
    @Schema(description = "유저아이디", example = "1")
    val userId: Long,
    @Schema(description = "프로필 URL")
    val profileImageUrl: String?,
    @Schema(description = "순위변동")
    val changeRanking: Long,
)
