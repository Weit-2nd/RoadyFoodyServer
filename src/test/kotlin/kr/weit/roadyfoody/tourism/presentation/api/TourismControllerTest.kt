package kr.weit.roadyfoody.tourism.presentation.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.verify
import kr.weit.roadyfoody.global.jsonmapper.ObjectMapperProvider
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.tourism.application.service.TourismService
import kr.weit.roadyfoody.tourism.fixture.TourismFixture.createSearchResponses
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerTest
@WebMvcTest(TourismController::class)
class TourismControllerTest(
    private val objectMapperProvider: ObjectMapperProvider,
    @MockkBean private val tourismService: TourismService,
    private val mockMvc: MockMvc,
) : BehaviorSpec({
        val requestPath = "/api/v1/tourism"

        given("GET $requestPath/search 테스트") {
            `when`("키워드로 관광지 검색 요청을 보내면") {
                every { tourismService.searchTourism(2, "강원") } returns createSearchResponses()
                then("200 상태 번호화 SearchResponses 반환한다.") {
                    mockMvc.perform(get("$requestPath/search?numOfRows=2&keyword=강원"))
                        .andExpect(status().isOk)
                        .andExpect(
                            content().json(
                                objectMapperProvider.objectMapper.writeValueAsString(
                                    createSearchResponses(),
                                ),
                            ),
                        )
                    verify(exactly = 1) { tourismService.searchTourism(2, "강원") }
                }
            }
        }
    })
