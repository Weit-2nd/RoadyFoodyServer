package kr.weit.roadyfoody.badge.service

import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.badge.domain.UserPromotionRewardHistory
import kr.weit.roadyfoody.badge.repository.UserPromotionRewardHistoryRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.rewards.domain.Rewards
import kr.weit.roadyfoody.rewards.repository.RewardsRepository
import kr.weit.roadyfoody.user.application.service.UserCommandService
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class BadgeCommandService(
    private val userCommandService: UserCommandService,
    private val foodSpotsReviewRepository: FoodSpotsReviewRepository,
    private val userPromotionRewardHistoryRepository: UserPromotionRewardHistoryRepository,
    private val rewardsRepository: RewardsRepository,
    private val userRepository: UserRepository,
) {
    fun tryChangeBadgeAndIfPromotedGiveBonus(userId: Long) {
        val user = userRepository.getByUserId(userId)
        val prevBadge = user.badge

        val reviews = foodSpotsReviewRepository.findByUser(user)
        val numOfReviews = reviews.size
        val numOfHighRatedReviews = reviews.count { it.rate >= Badge.HIGH_RATING_CONDITION }
        val newBadge = Badge.getBadge(numOfReviews, numOfHighRatedReviews)

        if (prevBadge == newBadge) {
            return
        }

        user.changeBadge(newBadge)

        if (Badge.isDemoted(prevBadge, user.badge) ||
            userPromotionRewardHistoryRepository.existsByUserIdAndBadge(user.id, user.badge)
        ) {
            return
        }

        userPromotionRewardHistoryRepository.save(UserPromotionRewardHistory.from(user))

        Rewards(
            user = user,
            foodSpotsHistory = null,
            rewardPoint = user.badge.bonusAmount,
            coinReceived = true,
            rewardType = user.badge.rewardType,
        ).also { rewardsRepository.save(it) }

        userCommandService.increaseCoin(user.id, user.badge.bonusAmount)
    }
}
