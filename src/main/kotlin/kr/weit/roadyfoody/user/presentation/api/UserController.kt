package kr.weit.roadyfoody.user.presentation.api

import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.user.application.dto.UserInfoResponse
import kr.weit.roadyfoody.user.application.service.UserQueryService
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.presentation.spec.UserControllerSpec
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
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
}
