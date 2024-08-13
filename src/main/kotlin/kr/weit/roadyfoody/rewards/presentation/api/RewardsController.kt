package kr.weit.roadyfoody.rewards.presentation.api

import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.rewards.application.dto.RewardsResponse
import kr.weit.roadyfoody.rewards.application.service.RewardsQueryService
import kr.weit.roadyfoody.rewards.presentation.spec.RewardsControllerSpec
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/rewards")
class RewardsController(
    private val rewardsQueryService: RewardsQueryService
) : RewardsControllerSpec {
    @GetMapping("/user")
    override fun getUserRewards(
        @LoginUser user: User,
        @PageableDefault pageable: Pageable
    ) : SliceResponse<RewardsResponse> {
        return rewardsQueryService.getUserRewards(user, pageable)
    }
}