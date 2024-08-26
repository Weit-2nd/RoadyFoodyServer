package kr.weit.roadyfoody.badge.repository

import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.badge.domain.UserPromotionRewardHistory
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserPromotionRewardHistoryRepository : JpaRepository<UserPromotionRewardHistory, Long> {
    fun existsByUserIdAndBadge(
        userId: Long,
        badge: Badge,
    ): Boolean

    fun deleteAllByUser(user: User)
}
