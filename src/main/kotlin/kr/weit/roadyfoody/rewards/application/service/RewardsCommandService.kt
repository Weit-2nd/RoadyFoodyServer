package kr.weit.roadyfoody.rewards.application.service

import kr.weit.roadyfoody.rewards.domain.Rewards
import kr.weit.roadyfoody.rewards.repository.RewardsRepository
import kr.weit.roadyfoody.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RewardsCommandService(
    private val rewardsRepository: RewardsRepository,
) {
    fun createRewards(rewards: Rewards) {
        rewardsRepository.save(rewards)
    }

    @Transactional
    fun deleteAllUserRewards(user: User) {
        rewardsRepository.deleteAllByUser(user)
    }
}
