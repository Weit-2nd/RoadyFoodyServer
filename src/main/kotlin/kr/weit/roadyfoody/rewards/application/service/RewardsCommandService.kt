package kr.weit.roadyfoody.rewards.application.service

import kr.weit.roadyfoody.rewards.domain.Rewards
import kr.weit.roadyfoody.rewards.repository.RewardsRepository
import org.springframework.stereotype.Service

@Service
class RewardsCommandService(
    private val rewardsRepository: RewardsRepository,
) {
    fun createRewards(rewards: Rewards) {
        rewardsRepository.save(rewards)
    }
}
