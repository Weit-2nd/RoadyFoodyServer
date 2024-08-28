package kr.weit.roadyfoody.badge.fixture

import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.badge.domain.UserPromotionRewardHistory
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser

const val TEST_INVALID_PROMOTION_REWARD_HISTORY_ID = 0L

fun createTestUserPromotionRewardHistory(
    id: Long = 0,
    user: User = createTestUser(),
    badge: Badge = Badge.BEGINNER,
) = UserPromotionRewardHistory(id = id, user = user, badge = badge)
