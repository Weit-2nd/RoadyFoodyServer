package kr.weit.roadyfoody.badge.domain

import kr.weit.roadyfoody.rewards.domain.RewardType

enum class Badge(
    val description: String,
) {
    BEGINNER("초심자"),
    PRO("중수"),
    SUPER("고수"),
    EXPERT("초고수"),
    ;

    companion object {
        const val BEGINNER_REVIEWS_THRESHOLD = 4
        const val BEGINNER_RATE_OVER_6_REVIEWS_THRESHOLD = 2
        const val PRO_REVIEWS_THRESHOLD = 9
        const val PRO_RATE_OVER_6_REVIEWS_THRESHOLD = 4
        const val SUPER_REVIEWS_THRESHOLD = 19
        const val SUPER_RATE_OVER_6_REVIEWS_THRESHOLD = 9

        const val BEGINNER_BONUS = 50
        const val PRO_BONUS = 150
        const val SUPER_BONUS = 250
        const val EXPERT_BONUS = 500

        fun getBadge(
            numOfReviews: Int,
            numOfReviewsRateOver6: Int,
        ): Badge =
            when {
                (numOfReviews <= BEGINNER_REVIEWS_THRESHOLD) or
                    (numOfReviewsRateOver6 <= BEGINNER_RATE_OVER_6_REVIEWS_THRESHOLD) -> BEGINNER
                (numOfReviews <= PRO_REVIEWS_THRESHOLD) or
                    (numOfReviewsRateOver6 <= PRO_RATE_OVER_6_REVIEWS_THRESHOLD) -> PRO
                (numOfReviews <= SUPER_REVIEWS_THRESHOLD) or
                    (numOfReviewsRateOver6 <= SUPER_RATE_OVER_6_REVIEWS_THRESHOLD) -> SUPER
                else -> EXPERT
            }

        fun isDemoted(
            prevBadge: Badge,
            newBadge: Badge,
        ): Boolean = prevBadge > newBadge

        fun calculateBonusAmount(badge: Badge): Int =
            when (badge) {
                BEGINNER -> BEGINNER_BONUS
                PRO -> PRO_BONUS
                SUPER -> SUPER_BONUS
                EXPERT -> EXPERT_BONUS
            }

        fun convertToReportType(badge: Badge): RewardType =
            when (badge) {
                BEGINNER -> RewardType.BEGINNER_GIFT
                PRO -> RewardType.PRO_GIFT
                SUPER -> RewardType.SUPER_GIFT
                EXPERT -> RewardType.EXPERT_GIFT
            }
    }
}
