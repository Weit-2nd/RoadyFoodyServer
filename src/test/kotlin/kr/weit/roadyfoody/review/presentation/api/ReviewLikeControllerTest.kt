package kr.weit.roadyfoody.review.presentation.api

import TEST_REVIEW_ID
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kr.weit.roadyfoody.review.application.service.ReviewLikeCommandService
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.deleteWithAuth
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
                `when`("리뷰 좋아요 생성 요청시") {
                    every { reviewLikeCommandService.likeReview(any(), any()) } just runs
                    then("리뷰 좋아요가 생성된다.") {
                        mockMvc
                            .perform(
                                postWithAuth(requestPath),
                            ).andExpect(status().isCreated)
                        verify { reviewLikeCommandService.likeReview(any(), any()) }
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

            given("DELETE $requestPath Test") {
                `when`("리뷰 좋아요 삭제 요청") {
                    every { reviewLikeCommandService.unlikeReview(any(), any()) } just runs
                    then("리뷰 좋아요가 삭제된다.") {
                        mockMvc
                            .perform(
                                deleteWithAuth(requestPath),
                            ).andExpect(status().isNoContent)
                        verify { reviewLikeCommandService.unlikeReview(any(), any()) }
                    }
                }

                `when`("리뷰 ID가 양수가 아닌 경우") {
                    then("400을 반환한다") {
                        mockMvc
                            .perform(
                                deleteWithAuth("/api/v1/reviews/-1/likes"),
                            ).andExpect(status().isBadRequest)
                    }
                }
            }
        },
    )
