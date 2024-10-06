package kr.weit.roadyfoody.review.presentation.api

import TEST_REVIEW_ID
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import kr.weit.roadyfoody.review.application.service.ReviewFlagCommandService
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.postWithAuth
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ReviewFlagController::class)
@ControllerTest
class ReviewFlagControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean private val reviewFlagCommandService: ReviewFlagCommandService,
) : BehaviorSpec({
        val requestPath = "/api/v1/reviews"

        given("POST $requestPath/{reviewId}/flag") {
            every { reviewFlagCommandService.flagReview(any(), any()) } just runs
            `when`("정상적인 데이터가 들어올 경우") {
                then("리뷰 신고가 성공하고 201 반환") {
                    mockMvc
                        .perform(
                            postWithAuth("$requestPath/$TEST_REVIEW_ID/flag"),
                        ).andExpect(status().isCreated)
                }
            }

            `when`("리뷰 id가 양수가 아닌 경우") {
                then("400 반환") {
                    mockMvc
                        .perform(
                            postWithAuth("$requestPath/-1/flag"),
                        ).andExpect(status().isBadRequest)
                }
            }
        }
    })
