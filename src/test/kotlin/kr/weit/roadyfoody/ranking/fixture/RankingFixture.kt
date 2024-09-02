package kr.weit.roadyfoody.ranking.fixture

import kr.weit.roadyfoody.ranking.dto.UserRanking
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser

fun createCountResponse(
    user: User = createTestUser(),
    total: Long = 10,
): UserRanking =
    UserRanking(
        userNickname = user.profile.nickname,
        total = total,
    )

fun createUserRankingResponse(): List<UserRanking> = listOf(createCountResponse())
