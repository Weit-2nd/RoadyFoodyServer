package kr.weit.roadyfoody.auth.presentation.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kr.weit.roadyfoody.auth.application.service.AuthCommandService
import kr.weit.roadyfoody.auth.application.service.AuthQueryService
import kr.weit.roadyfoody.auth.dto.DuplicatedNicknameResponse
import kr.weit.roadyfoody.auth.dto.SignUpRequest
import kr.weit.roadyfoody.auth.fixture.TEST_BEARER_TOKEN
import kr.weit.roadyfoody.auth.fixture.createProfileImageFile
import kr.weit.roadyfoody.auth.fixture.createSignUpRequestFile
import kr.weit.roadyfoody.auth.fixture.createTestSignUpRequest
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.ImageFormat.GIF
import kr.weit.roadyfoody.support.utils.ImageFormat.JPEG
import kr.weit.roadyfoody.support.utils.ImageFormat.PNG
import kr.weit.roadyfoody.support.utils.ImageFormat.WEBP
import kr.weit.roadyfoody.user.fixture.TEST_USER_NICKNAME
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.multipart.MultipartFile

@WebMvcTest(AuthController::class)
@ControllerTest
class AuthControllerTest(
    private val objectMapper: ObjectMapper,
    @MockkBean private val authCommandService: AuthCommandService,
    @MockkBean private val authQueryService: AuthQueryService,
    private val mockMvc: MockMvc,
) : BehaviorSpec({
        val requestPath = "/api/v1/auth"

        given("POST $requestPath") {
            `when`("WEBP 이미지 프로필 사진을 업로드하면") {
                every { authCommandService.register(any<String>(), any<SignUpRequest>(), any<MultipartFile>()) } just runs
                then("회원가입에 성공한다") {
                    mockMvc.perform(
                        multipart(requestPath)
                            .file(createProfileImageFile(WEBP))
                            .file(createSignUpRequestFile(objectMapper.writeValueAsString(createTestSignUpRequest()).byteInputStream()))
                            .header(AUTHORIZATION, TEST_BEARER_TOKEN)
                            .contentType(MediaType.MULTIPART_FORM_DATA),
                    ).andExpect(status().isCreated)
                    verify(exactly = 1) { authCommandService.register(any<String>(), any<SignUpRequest>(), any<MultipartFile>()) }
                }
            }

            `when`("프로필사진을 업로드하지 않아도") {
                every { authCommandService.register(any<String>(), any<SignUpRequest>(), any<MultipartFile>()) } just runs
                then("회원가입에 성공한다") {
                    mockMvc.perform(
                        multipart(requestPath)
                            .file(createSignUpRequestFile(objectMapper.writeValueAsString(createTestSignUpRequest()).byteInputStream()))
                            .header(AUTHORIZATION, TEST_BEARER_TOKEN)
                            .contentType(MediaType.MULTIPART_FORM_DATA),
                    ).andExpect(status().isCreated)
                    verify(exactly = 1) { authCommandService.register(any<String>(), any<SignUpRequest>(), any<MultipartFile>()) }
                }
            }

            `when`("WEBP 이미지가 아닌 프로필 사진을 업로드하면") {
                every { authCommandService.register(any<String>(), any<SignUpRequest>(), any<MultipartFile>()) } just runs
                then("회원가입에 실패한다") {
                    forAll(
                        row(JPEG),
                        row(PNG),
                        row(GIF),
                    ) { format ->
                        mockMvc.perform(
                            multipart(requestPath)
                                .file(createProfileImageFile(format))
                                .file(createSignUpRequestFile(objectMapper.writeValueAsString(createTestSignUpRequest()).byteInputStream()))
                                .header(AUTHORIZATION, TEST_BEARER_TOKEN)
                                .contentType(MediaType.MULTIPART_FORM_DATA),
                        ).andExpect(status().isBadRequest)
                    }
                    verify(exactly = 0) { authCommandService.register(any<String>(), any<SignUpRequest>(), any<MultipartFile>()) }
                }
            }

            `when`("소셜 로그인 AccessToken 이 없으면") {
                every { authCommandService.register(any<String>(), any<SignUpRequest>(), any<MultipartFile>()) } just runs
                then("회원가입에 실패한다") {
                    mockMvc.perform(
                        multipart(requestPath)
                            .file(createProfileImageFile(WEBP))
                            .file(createSignUpRequestFile(objectMapper.writeValueAsString(createTestSignUpRequest()).byteInputStream()))
                            .contentType(MediaType.MULTIPART_FORM_DATA),
                    ).andExpect(status().isBadRequest)
                    verify(exactly = 0) { authCommandService.register(any<String>(), any<SignUpRequest>(), any<MultipartFile>()) }
                }
            }
        }

        given("GET $requestPath/check-nickname") {
            `when`("중복되지 않는 닉네임을 전달하면") {
                every { authQueryService.checkDuplicatedNickname(any()) } returns false
                then("내용이 false 인 응답을 반환한다") {
                    mockMvc.perform(
                        get("$requestPath/check-nickname")
                            .param("nickname", TEST_USER_NICKNAME),
                    )
                        .andExpect(status().isOk)
                        .andExpect {
                            content().json(
                                objectMapper.writeValueAsString(DuplicatedNicknameResponse(false)),
                            )
                        }
                }
            }

            `when`("중복되는 닉네임을 전달하면") {
                every { authQueryService.checkDuplicatedNickname(any()) } returns true
                then("내용이 true 인 응답을 반환한다") {
                    mockMvc.perform(
                        get("$requestPath/check-nickname")
                            .param("nickname", TEST_USER_NICKNAME),
                    )
                        .andExpect(status().isOk)
                        .andExpect {
                            content().json(
                                objectMapper.writeValueAsString(DuplicatedNicknameResponse(true)),
                            )
                        }
                }
            }
        }
    })
