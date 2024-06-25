package kr.weit.roadyfoody.tourism.fixture

import com.fasterxml.jackson.databind.ObjectMapper
import kr.weit.roadyfoody.tourism.dto.ResponseWrapper
import kr.weit.roadyfoody.tourism.dto.SearchResponse
import kr.weit.roadyfoody.tourism.dto.SearchResponses
import kr.weit.roadyfoody.tourism.dto.TourismType
import org.springframework.core.io.ClassPathResource

object TourismFixture {
    private val objectMapper = ObjectMapper()

    private const val TOUR_RESPONSE_SIZE_10 = "payload/tour-search-response.json"
    private const val FESTIVAL_RESPONSE_SIZE_9 = "payload/festival-search-response.json"
    private const val TOUR_RESPONSE_SIZE_2 = "payload/tour-search-response-size-2.json"
    private const val FESTIVAL_RESPONSE_SIZE_2 = "payload/festival-search-response-size-2.json"
    private const val TOUR_RESPONSE_SIZE_0 = "payload/tour-search-response-size-0.json"
    private const val FESTIVAL_RESPONSE_SIZE_0 = "payload/festival-search-response-size-0.json"

    // json 파일을 읽어와서 ResponseWrapper 객체로 변환
    private fun loadResponse(resourcePath: String): ResponseWrapper {
        val resource = ClassPathResource(resourcePath)
        return objectMapper.readValue(resource.inputStream, ResponseWrapper::class.java)
    }

    fun loadTourResponseSize10(): ResponseWrapper {
        return loadResponse(TOUR_RESPONSE_SIZE_10)
    }

    fun loadFestivalResponseSize9(): ResponseWrapper {
        return loadResponse(FESTIVAL_RESPONSE_SIZE_9)
    }

    fun loadTourResponseSize2(): ResponseWrapper {
        return loadResponse(TOUR_RESPONSE_SIZE_2)
    }

    fun loadFestivalResponseSize2(): ResponseWrapper {
        return loadResponse(FESTIVAL_RESPONSE_SIZE_2)
    }

    fun loadTourResponseSize0(): ResponseWrapper {
        return loadResponse(TOUR_RESPONSE_SIZE_0)
    }

    fun loadFestivalResponseSize0(): ResponseWrapper {
        return loadResponse(FESTIVAL_RESPONSE_SIZE_0)
    }

    //
    fun createSearchResponses(): SearchResponses {
        return SearchResponses(
            listOf(
                SearchResponse(
                    title = "title",
                    mainAddr = "addr1",
                    secondaryAddr = "addr2",
                    longitude = 1.0,
                    latitude = 1.0,
                    tel = "tel",
                    thumbnailImage = "firstimage2",
                    tourismType = TourismType.TOUR,
                ),
                SearchResponse(
                    title = "title",
                    mainAddr = "addr1",
                    secondaryAddr = "addr2",
                    longitude = 1.0,
                    latitude = 1.0,
                    tel = "tel",
                    thumbnailImage = "firstimage2",
                    tourismType = TourismType.FESTIVAL,
                ),
            ),
        )
    }
}
