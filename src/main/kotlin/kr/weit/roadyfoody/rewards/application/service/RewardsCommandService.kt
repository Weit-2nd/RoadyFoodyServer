package kr.weit.roadyfoody.rewards.application.service

import jakarta.transaction.Transactional
import kr.weit.roadyfoody.rewards.domain.Rewards
import kr.weit.roadyfoody.rewards.repository.RewardsRepository
import org.springframework.stereotype.Service

@Service
class RewardsCommandService(
    private val rewardsRepository: RewardsRepository
){
    @Transactional
    fun createRewards(rewards : Rewards) {
        rewardsRepository.save(rewards)
    }
}