package kr.weit.roadyfoody.auth.presentation.api

import io.awspring.cloud.s3.S3Template
import io.kotest.core.spec.style.BehaviorSpec
import kr.weit.roadyfoody.auth.domain.SocialAccessToken
import kr.weit.roadyfoody.auth.dto.KakaoUserResponse
import kr.weit.roadyfoody.auth.fixture.TEST_BEARER_TOKEN
import kr.weit.roadyfoody.auth.fixture.TEST_WEBP_IMAGE_BYTES
import kr.weit.roadyfoody.auth.fixture.createTestKakaoUserResponse
import kr.weit.roadyfoody.auth.presentation.client.KakaoClientInterface
import kr.weit.roadyfoody.global.config.S3Properties
import kr.weit.roadyfoody.support.annotation.ControllerIntegrateTest
import kr.weit.roadyfoody.term.fixture.createTestTerms
import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.user.fixture.TEST_USER_NICKNAME
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerIntegrateTest
class AuthIntegrationControllerTest(
    private val s3Template: S3Template,
    private val s3Properties: S3Properties,
    private val userRepository: UserRepository,
    private val termRepository: TermRepository,
    private val mockMvc: MockMvc,
) : BehaviorSpec({
        lateinit var validTermIdSet: Set<Long>
        beforeSpec {
            s3Template.createBucket(s3Properties.bucket)
            validTermIdSet = termRepository.saveAll(createTestTerms()).map { it.id }.toSet()
        }
        afterEach {
            userRepository.findAll()
                .forEach {
                    it.profile.profileImageName?.let { imageName ->
                        s3Template.deleteObject(s3Properties.bucket, imageName)
                    }
                }
        }
        afterSpec {
            termRepository.deleteAll()
            s3Template.deleteBucket(s3Properties.bucket)
        }

        given("필수약관을 동의하지 않은 경우") {
            `when`("POST /api/v1/auth 요청하면") {
                then("실패한다.") {
                    mockMvc
                        .perform(
                            multipart("/api/v1/auth")
                                .file("profileImage", TEST_WEBP_IMAGE_BYTES)
                                .param("nickname", TEST_USER_NICKNAME)
                                .param("agreedTermIds", "")
                                .header(AUTHORIZATION, TEST_BEARER_TOKEN)
                                .contentType(MediaType.MULTIPART_FORM_DATA),
                        ).andExpect(status().isBadRequest)
                }
            }
        }

        given("필수약관을 동의한 경우") {
            `when`("POST /api/v1/auth 요청하면") {
                then("성공한다.") {
                    mockMvc
                        .perform(
                            multipart("/api/v1/auth")
                                .file("profileImage", TEST_WEBP_IMAGE_BYTES)
                                .param("nickname", TEST_USER_NICKNAME)
                                .param("agreedTermIds", validTermIdSet.joinToString())
                                .header(AUTHORIZATION, TEST_BEARER_TOKEN)
                                .contentType(MediaType.MULTIPART_FORM_DATA),
                        ).andExpect(status().isCreated)
                }
            }
        }
    })

@Profile("test")
@Primary
@Component
class MockKakaoClient : KakaoClientInterface {
    override fun requestUserInfo(socialAccessToken: SocialAccessToken): KakaoUserResponse {
        return createTestKakaoUserResponse()
    }
}
