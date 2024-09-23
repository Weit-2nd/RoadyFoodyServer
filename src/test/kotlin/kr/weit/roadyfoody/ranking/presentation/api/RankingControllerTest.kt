package kr.weit.roadyfoody.ranking.presentation.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import kr.weit.roadyfoody.global.TEST_PAGE_SIZE
import kr.weit.roadyfoody.global.TEST_START_INDEX
import kr.weit.roadyfoody.ranking.application.service.RankingQueryService
import kr.weit.roadyfoody.ranking.fixture.createUserRankingResponse
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.getWithAuth
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RankingController::class)
@ControllerTest
class RankingControllerTest(
    @MockkBean private val rankingQueryService: RankingQueryService,
    private val mockMvc: MockMvc,
) : BehaviorSpec(
        {
            val requestPath = "/api/v1/ranking"

            given("GET $requestPath/report") {
                val response = createUserRankingResponse()
                every {
                    rankingQueryService.getReportRanking(any(), any())
                } returns response
                `when`("정상적인 데이터가 들어올 경우") {
                    then("리포트 랭킹 리스트가 조회된다.") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/report")
                                    .param("size", "$TEST_PAGE_SIZE")
                                    .param("start", "$TEST_START_INDEX"),
                            ).andExpect(status().isOk)
                    }
                }

                `when`("size에 음수가 들어올 경우") {
                    then("400을 반환한다") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/report")
                                    .param("size", "-1")
                                    .param("start", "$TEST_START_INDEX"),
                            ).andExpect(status().isBadRequest)
                    }
                }

                `when`("start에 음수가 들어올 경우") {
                    then("400을 반환한다") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/report")
                                    .param("size", "$TEST_PAGE_SIZE")
                                    .param("start", "-1"),
                            ).andExpect(status().isBadRequest)
                    }
                }
            }

            given("GET $requestPath/review") {
                val response = createUserRankingResponse()
                every {
                    rankingQueryService.getReviewRanking(any(), any())
                } returns response
                `when`("정상적인 데이터가 들어올 경우") {
                    then("리뷰 랭킹 리스트가 조회된다.") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/review")
                                    .param("size", "$TEST_PAGE_SIZE")
                                    .param("start", "$TEST_START_INDEX"),
                            ).andExpect(status().isOk)
                    }
                }

                `when`("size에 음수가 들어올 경우") {
                    then("400을 반환한다") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/review")
                                    .param("size", "-1")
                                    .param("start", "$TEST_START_INDEX"),
                            ).andExpect(status().isBadRequest)
                    }
                }

                `when`("start에 음수가 들어올 경우") {
                    then("400을 반환한다") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/review")
                                    .param("size", "$TEST_PAGE_SIZE")
                                    .param("start", "-1"),
                            ).andExpect(status().isBadRequest)
                    }
                }
            }

            given("GET $requestPath/like") {
                val response = createUserRankingResponse()
                every {
                    rankingQueryService.getLikeRanking(any(), any())
                } returns response
                `when`("정상적인 데이터가 들어올 경우") {
                    then("좋아요 랭킹 리스트가 조회된다.") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/like")
                                    .param("size", "$TEST_PAGE_SIZE")
                                    .param("start", "$TEST_START_INDEX"),
                            ).andExpect(status().isOk)
                    }
                }

                `when`("size가 음수가 들어올 경우") {
                    then("400을 반환한다") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/like")
                                    .param("size", "-1")
                                    .param("start", "$TEST_START_INDEX"),
                            ).andExpect(status().isBadRequest)
                    }
                }

                `when`("start에 음수가 들어올 경우") {
                    then("400을 반환한다") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/like")
                                    .param("size", "$TEST_PAGE_SIZE")
                                    .param("start", "-1"),
                            ).andExpect(status().isBadRequest)
                    }
                }
            }

            given("GET $requestPath/total") {
                val response = createUserRankingResponse()
                every {
                    rankingQueryService.getTotalRanking(any(), any())
                } returns response
                `when`("정상적인 데이터가 들어올 경우") {
                    then("좋아요 랭킹 리스트가 조회된다.") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/total")
                                    .param("size", "$TEST_PAGE_SIZE")
                                    .param("start", "$TEST_START_INDEX"),
                            ).andExpect(status().isOk)
                    }
                }

                `when`("size가 음수가 들어올 경우") {
                    then("400을 반환한다") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/total")
                                    .param("size", "-1")
                                    .param("start", "$TEST_START_INDEX"),
                            ).andExpect(status().isBadRequest)
                    }
                }

                `when`("start에 음수가 들어올 경우") {
                    then("400을 반환한다") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/total")
                                    .param("size", "$TEST_PAGE_SIZE")
                                    .param("start", "-1"),
                            ).andExpect(status().isBadRequest)
                    }
                }
            }
        },
    )
