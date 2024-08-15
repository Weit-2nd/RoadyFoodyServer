package kr.weit.roadyfoody.rewards.application.service

import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.rewards.application.dto.RewardsResponse
import kr.weit.roadyfoody.rewards.repository.RewardsRepository
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RewardsQueryService(
    private val rewardsRepository: RewardsRepository,
) {
    @Transactional(readOnly = true)
    fun getUserRewards(
        user: User,
        pageable: Pageable,
    ): SliceResponse<RewardsResponse> {
        val response =
            rewardsRepository
                .findAllByUser(user, pageable)
                .map(RewardsResponse::from)
        return SliceResponse(response)
    }
}
