package kr.weit.roadyfoody.badge.domain

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import kr.weit.roadyfoody.common.domain.BaseTimeEntity
import kr.weit.roadyfoody.user.domain.User

@Entity
@Table(
    name = "user_promotion_reward_histories",
    uniqueConstraints = [
        UniqueConstraint(name = "user_promotion_reward_histories_user_id_promotion_reward_idx", columnNames = ["user_id", "badge"]),
    ],
)
@SequenceGenerator(
    name = "USER_PROMOTION_REWARD_HISTORIES_SEQ_GENERATOR",
    sequenceName = "USER_PROMOTION_REWARD_HISTORIES_SEQ",
    initialValue = 1,
    allocationSize = 1,
)
class UserPromotionRewardHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_PROMOTION_REWARD_HISTORIES_SEQ_GENERATOR")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,
    @Enumerated(EnumType.STRING)
    val badge: Badge,
) : BaseTimeEntity() {
    companion object {
        fun from(user: User): UserPromotionRewardHistory = UserPromotionRewardHistory(user = user, badge = user.badge)
    }
}
