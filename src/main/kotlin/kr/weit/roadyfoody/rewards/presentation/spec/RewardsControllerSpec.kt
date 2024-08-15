package kr.weit.roadyfoody.rewards.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.rewards.application.dto.RewardsResponse
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault

@Tag(name = SwaggerTag.REWARDS)
interface RewardsControllerSpec {
    @Operation(
        description = "유저의 리워드 리스트 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리워드 리스트 조회 성공",
            ),
        ],
    )
    fun getUserRewards(
        @LoginUser
        user: User,
        @PageableDefault pageable: Pageable,
    ): SliceResponse<RewardsResponse>
}
