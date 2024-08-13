package kr.weit.roadyfoody.reward.repository

import kr.weit.roadyfoody.reward.domain.Rewards
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface RewardsRepository : JpaRepository<Rewards, Long> {
    fun findAllByUser(user: User, pageable: Pageable) : Slice<Rewards>
}