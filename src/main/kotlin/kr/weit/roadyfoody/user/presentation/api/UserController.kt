package kr.weit.roadyfoody.user.presentation.api

import jakarta.validation.constraints.Positive
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.user.application.dto.UserInfoResponse
import kr.weit.roadyfoody.user.application.dto.UserReportHistoriesResponse
import kr.weit.roadyfoody.user.application.service.UserQueryService
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.presentation.spec.UserControllerSpec
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userQueryService: UserQueryService,
) : UserControllerSpec {
    @GetMapping("/me")
    override fun getLoginUserInfo(
        @LoginUser user: User,
    ): UserInfoResponse = userQueryService.getUserInfo(user)

    @GetMapping("{userId}/food-spots/histories")
    override fun getReportHistories(
        @PathVariable("userId")
        userId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @Positive(message = "마지막 ID는 양수여야 합니다.")
        @RequestParam(required = false)
        lastId: Long?,
    ): SliceResponse<UserReportHistoriesResponse> = userQueryService.getReportHistories(userId, size, lastId)
}
