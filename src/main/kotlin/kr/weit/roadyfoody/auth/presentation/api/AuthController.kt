package kr.weit.roadyfoody.auth.presentation.api

import jakarta.validation.Valid
import kr.weit.roadyfoody.auth.application.service.AuthCommandService
import kr.weit.roadyfoody.auth.domain.SocialAccessToken
import kr.weit.roadyfoody.auth.dto.SignUpRequest
import kr.weit.roadyfoody.auth.presentation.spec.AuthControllerSpec
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    val authCommandService: AuthCommandService,
) : AuthControllerSpec {
    @ResponseStatus(CREATED)
    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    override fun signUp(
        @RequestHeader(AUTHORIZATION) socialAccessToken: String?,
        @Valid signUpRequest: SignUpRequest,
    ) {
        requireNotNull(socialAccessToken) { "socialAccessToken 가 존재하지 않습니다." }
        authCommandService.register(SocialAccessToken(socialAccessToken), signUpRequest)
    }
}
