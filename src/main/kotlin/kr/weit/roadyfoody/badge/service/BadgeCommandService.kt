package kr.weit.roadyfoody.badge.service

import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.badge.domain.UserPromotionRewardHistory
import kr.weit.roadyfoody.badge.repository.UserPromotionRewardHistoryRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.rewards.domain.Rewards
import kr.weit.roadyfoody.rewards.repository.RewardsRepository
import kr.weit.roadyfoody.user.application.service.UserCommandService
import kr.weit.roadyfoody.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class BadgeCommandService(
    private val userCommandService: UserCommandService,
    private val foodSpotsReviewRepository: FoodSpotsReviewRepository,
    private val userPromotionRewardHistoryRepository: UserPromotionRewardHistoryRepository,
    private val rewardsRepository: RewardsRepository,
) {
    fun tryChangeBadgeAndIfPromotedGiveBonus(user: User) {
        val prevBadge = user.badge

        val reviews = foodSpotsReviewRepository.findByUser(user)
        val numOfReviews = reviews.size
        val numOfReviewsRateOver6 = reviews.count { it.rate >= 6 }
        val newBadge = Badge.getBadge(numOfReviews, numOfReviewsRateOver6)

        if (prevBadge == newBadge) {
            return
        }

        // 최하단 increaseCoin 에서 user 의 coin 을 변경하는 새로운 트랜잭션을 만듭니다.
        // user 내부 badge 를 현재 트랜잭션에서 변경하게되면 데드락이 발생합니다. 따라서 새로운 트랜잭션을 만들어야합니다.
        val changedUser = userCommandService.changeBadgeNewTx(user.id, newBadge)

        if (Badge.isDemoted(prevBadge, changedUser.badge) ||
            userPromotionRewardHistoryRepository.existsByUserIdAndBadge(changedUser.id, changedUser.badge)
        ) {
            return
        }

        userPromotionRewardHistoryRepository.save(UserPromotionRewardHistory.from(changedUser))

        val bonusAmount = Badge.calculateBonusAmount(changedUser.badge)

        Rewards(
            user = changedUser,
            foodSpotsHistory = null,
            rewardPoint = bonusAmount,
            coinReceived = true,
            rewardType = Badge.convertToReportType(changedUser.badge),
        ).also { rewardsRepository.save(it) }

        userCommandService.increaseCoin(changedUser.id, bonusAmount)
    }
}
