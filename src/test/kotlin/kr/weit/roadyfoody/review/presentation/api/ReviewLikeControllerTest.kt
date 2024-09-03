package kr.weit.roadyfoody.review.presentation.api

import TEST_REVIEW_ID
import com.ninjasquad.springmockk.MockkBean
import createTestToggleLikeResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.verify
import kr.weit.roadyfoody.review.application.service.ReviewLikeCommandService
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.postWithAuth
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ReviewLikeController::class)
@ControllerTest
class ReviewLikeControllerTest(
    @MockkBean private val reviewLikeCommandService: ReviewLikeCommandService,
    private val mockMvc: MockMvc,
) : BehaviorSpec(
        {
            val reviewId = TEST_REVIEW_ID
            val requestPath = "/api/v1/reviews/$reviewId/likes"
            given("POST $requestPath Test") {
                `when`("요청이 들어오면") {
                    every {
                        reviewLikeCommandService.toggleLike(
                            any(),
                            any(),
                        )
                    } returns createTestToggleLikeResponse()
                    then("좋아요 수와 좋아요 여부를 반환한다") {
                        mockMvc
                            .perform(
                                postWithAuth(requestPath),
                            ).andExpect(status().isOk)
                        verify(exactly = 1) { reviewLikeCommandService.toggleLike(any(), any()) }
                    }
                }

                `when`("리뷰 ID가 양수가 아닌 경우") {
                    then("400을 반환한다") {
                        mockMvc
                            .perform(
                                postWithAuth("/api/v1/reviews/-1/likes"),
                            ).andExpect(status().isBadRequest)
                    }
                }
            }
        },
    )
