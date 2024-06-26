package kr.weit.roadyfoody.auth.presentation.api

import jakarta.validation.Valid
import kr.weit.roadyfoody.auth.application.dto.DuplicatedNicknameResponse
import kr.weit.roadyfoody.auth.application.dto.SignUpRequest
import kr.weit.roadyfoody.auth.application.service.AuthCommandService
import kr.weit.roadyfoody.auth.application.service.AuthQueryService
import kr.weit.roadyfoody.auth.presentation.spec.AuthControllerSpec
import kr.weit.roadyfoody.global.validator.MaxFileSize
import kr.weit.roadyfoody.global.validator.WebPImage
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    val authCommandService: AuthCommandService,
    val authQueryService: AuthQueryService,
) : AuthControllerSpec {
    @ResponseStatus(CREATED)
    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    override fun signUp(
        @RequestHeader(AUTHORIZATION) socialAccessToken: String?,
        @Valid
        @RequestPart
        signUpRequest: SignUpRequest,
        @MaxFileSize
        @WebPImage
        @RequestPart(required = false)
        profileImage: MultipartFile?,
    ) {
        requireNotNull(socialAccessToken) { "socialAccessToken 가 존재하지 않습니다." }
        authCommandService.register(socialAccessToken, signUpRequest, profileImage)
    }

    @GetMapping("/check-nickname")
    override fun checkDuplicatedNickname(
        @RequestParam nickname: String,
    ): DuplicatedNicknameResponse = authQueryService.checkDuplicatedNickname(nickname)
}
