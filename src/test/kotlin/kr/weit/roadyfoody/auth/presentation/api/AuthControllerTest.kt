package kr.weit.roadyfoody.auth.presentation.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kr.weit.roadyfoody.auth.application.service.AuthCommandService
import kr.weit.roadyfoody.auth.domain.SocialAccessToken
import kr.weit.roadyfoody.auth.dto.SignUpRequest
import kr.weit.roadyfoody.auth.fixture.TEST_BEARER_TOKEN
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.support.utils.generateImageBytes
import kr.weit.roadyfoody.term.fixture.createTestRequiredTermIdSet
import kr.weit.roadyfoody.user.fixture.TEST_USER_NICKNAME
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthController::class)
@ControllerTest
class AuthControllerTest(
    @MockkBean private val authCommandService: AuthCommandService,
    private val mockMvc: MockMvc,
) : BehaviorSpec({
        val requestPath = "/api/v1/auth"

        given("POST $requestPath") {
            `when`("${ImageFormat.WEBP.getStrValues()} 이미지 프로필 사진을 업로드하면") {
                every { authCommandService.register(any<SocialAccessToken>(), any<SignUpRequest>()) } just runs
                then("회원가입에 성공한다") {
                    mockMvc.perform(
                        multipart(requestPath)
                            .file("profileImage", generateImageBytes(ImageFormat.WEBP))
                            .param("nickname", TEST_USER_NICKNAME)
                            .param("agreedTermIds", createTestRequiredTermIdSet().joinToString())
                            .header(AUTHORIZATION, TEST_BEARER_TOKEN)
                            .contentType(MediaType.MULTIPART_FORM_DATA),
                    ).andExpect(status().isCreated)
                    verify(exactly = 1) { authCommandService.register(any<SocialAccessToken>(), any<SignUpRequest>()) }
                }
            }

            `when`("프로필사진을 업로드하지 않아도") {
                every { authCommandService.register(any<SocialAccessToken>(), any<SignUpRequest>()) } just runs
                then("회원가입에 성공한다") {
                    mockMvc.perform(
                        multipart(requestPath)
                            .param("nickname", TEST_USER_NICKNAME)
                            .param("agreedTermIds", createTestRequiredTermIdSet().joinToString())
                            .header(AUTHORIZATION, TEST_BEARER_TOKEN)
                            .contentType(MediaType.MULTIPART_FORM_DATA),
                    ).andExpect(status().isCreated)
                    verify(exactly = 1) { authCommandService.register(any<SocialAccessToken>(), any<SignUpRequest>()) }
                }
            }

            `when`("${ImageFormat.WEBP.first()} 이미지가 아닌 프로필 사진을 업로드하면") {
                every { authCommandService.register(any<SocialAccessToken>(), any<SignUpRequest>()) } just runs
                then("회원가입에 실패한다") {
                    forAll(
                        row(ImageFormat.JPEG),
                        row(ImageFormat.PNG),
                        row(ImageFormat.GIF),
                    ) { format ->
                        mockMvc.perform(
                            multipart(requestPath)
                                .file("profileImage", generateImageBytes(format))
                                .param("nickname", TEST_USER_NICKNAME)
                                .param("agreedTermIds", createTestRequiredTermIdSet().joinToString())
                                .header(AUTHORIZATION, TEST_BEARER_TOKEN)
                                .contentType(MediaType.MULTIPART_FORM_DATA),
                        ).andExpect(status().isBadRequest)
                    }
                    verify(exactly = 0) { authCommandService.register(any<SocialAccessToken>(), any<SignUpRequest>()) }
                }
            }

            `when`("닉네임을 입력하지 않으면") {
                every { authCommandService.register(any<SocialAccessToken>(), any<SignUpRequest>()) } just runs
                then("회원가입에 실패한다") {
                    mockMvc.perform(
                        multipart(requestPath)
                            .file("profileImage", generateImageBytes(ImageFormat.WEBP))
                            .param("agreedTermIds", createTestRequiredTermIdSet().joinToString())
                            .header(AUTHORIZATION, TEST_BEARER_TOKEN)
                            .contentType(MediaType.MULTIPART_FORM_DATA),
                    ).andExpect(status().isBadRequest)
                    verify(exactly = 0) { authCommandService.register(any<SocialAccessToken>(), any<SignUpRequest>()) }
                }
            }

            `when`("소셜 로그인 AccessToken 이 없으면") {
                every { authCommandService.register(any(), any()) } just runs
                then("회원가입에 실패한다") {
                    mockMvc.perform(
                        multipart(requestPath)
                            .file("profileImage", generateImageBytes(ImageFormat.WEBP))
                            .param("nickname", TEST_USER_NICKNAME)
                            .param("agreedTermIds", createTestRequiredTermIdSet().joinToString())
                            .contentType(MediaType.MULTIPART_FORM_DATA),
                    ).andExpect(status().isBadRequest)
                    verify(exactly = 0) { authCommandService.register(any<SocialAccessToken>(), any<SignUpRequest>()) }
                }
            }
        }
    })
