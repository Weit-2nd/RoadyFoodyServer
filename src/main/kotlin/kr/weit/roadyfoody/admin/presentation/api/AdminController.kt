package kr.weit.roadyfoody.admin.presentation.api

import kr.weit.roadyfoody.admin.application.service.AdminCommandService
import kr.weit.roadyfoody.admin.application.service.AdminQueryService
import kr.weit.roadyfoody.admin.dto.SimpleUserInfoResponses
import kr.weit.roadyfoody.admin.dto.UserAccessTokenResponse
import kr.weit.roadyfoody.admin.presentation.spec.AdminControllerSpec
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Profile("!stable")
@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val adminCommandService: AdminCommandService,
    private val adminQueryService: AdminQueryService,
) : AdminControllerSpec {
    @GetMapping("/users")
    override fun getUserInfoList(
        @PageableDefault pageable: Pageable,
    ): SimpleUserInfoResponses = adminQueryService.getUserInfoList(pageable)

    @GetMapping("/users/{userId}/token")
    override fun getUserAccessToken(
        @PathVariable userId: Long,
    ): UserAccessTokenResponse = adminCommandService.getUserAccessToken(userId)
}
