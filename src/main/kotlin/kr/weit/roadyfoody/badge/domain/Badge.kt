package kr.weit.roadyfoody.badge.domain

import kr.weit.roadyfoody.rewards.domain.RewardType

enum class Badge(
    val description: String,
    val totalReviewsRequired: Int,
    val highRatedReviewsRequired: Int,
    val bonusAmount: Int,
    val rewardType: RewardType,
) {
    BEGINNER("초심자", 0, 0, 50, RewardType.BEGINNER_GIFT),
    PRO("중수", 5, 3, 150, RewardType.PRO_GIFT),
    SUPER("고수", 10, 5, 250, RewardType.SUPER_GIFT),
    EXPERT("초고수", 20, 10, 500, RewardType.EXPERT_GIFT),
    ;

    companion object {
        const val HIGH_RATING_CONDITION = 6

        fun getBadge(
            totalReviews: Int,
            highRatedReviews: Int,
        ): Badge =
            entries.lastOrNull { badge ->
                totalReviews >= badge.totalReviewsRequired &&
                    highRatedReviews >= badge.highRatedReviewsRequired
            } ?: BEGINNER

        fun isDemoted(
            prevBadge: Badge,
            newBadge: Badge,
        ): Boolean = prevBadge > newBadge
    }
}
