package kr.weit.roadyfoody.ranking.fixture

import kr.weit.roadyfoody.ranking.dto.UserRanking
import kr.weit.roadyfoody.ranking.dto.UserRankingResponse
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser

fun createCount(
    user: User = createTestUser(),
    total: Long = 10,
): UserRanking =
    UserRanking(
        userNickname = user.profile.nickname,
        total = total,
    )

fun createUserRanking(): List<UserRanking> = listOf(createCount())

fun createCountResponse(
    ranking: Long = 1,
    user: User = createTestUser(),
    total: Long = 10,
): UserRankingResponse =
    UserRankingResponse(
        ranking = ranking,
        userNickname = user.profile.nickname,
        total = total,
    )

fun createUserRankingResponse(): List<UserRankingResponse> = listOf(createCountResponse())
