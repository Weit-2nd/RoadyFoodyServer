package kr.weit.roadyfoody.reward.application.service

import jakarta.transaction.Transactional
import kr.weit.roadyfoody.reward.domain.Rewards
import kr.weit.roadyfoody.reward.repository.RewardsRepository
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