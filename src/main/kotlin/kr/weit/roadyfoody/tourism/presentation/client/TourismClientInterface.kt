package kr.weit.roadyfoody.tourism.presentation.client

import kr.weit.roadyfoody.common.annotation.ClientInterface
import kr.weit.roadyfoody.tourism.dto.ResponseWrapper
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange

@ClientInterface
interface TourismClientInterface {
    @GetExchange(
        "/searchKeyword1?" +
            "serviceKey={SERVICE_KEY}" +
            "&MobileApp={MOBILE_APP}&MobileOS={MOBILE_OS}&pageNo=1&numOfRows={NUM_OF_ROWS}&listYN=Y" +
            "&&arrange=A&contentTypeId={CONTENT_TYPE_ID}&keyword={KEYWORD}&_type=json",
    )
    fun searchTourismKeyword(
        @PathVariable(name = "SERVICE_KEY") serviceKey: String,
        @PathVariable(name = "MOBILE_APP") mobileApp: String,
        @PathVariable(name = "MOBILE_OS") mobileOs: String,
        @PathVariable(name = "NUM_OF_ROWS") numOfRows: Int,
        @PathVariable(name = "KEYWORD") keyword: String,
        @PathVariable(name = "CONTENT_TYPE_ID") contentTypeId: Int = 15,
    ): ResponseWrapper

    @GetExchange(
        "/searchKeyword1?" +
            "serviceKey={SERVICE_KEY}" +
            "&MobileApp={MOBILE_APP}&MobileOS={MOBILE_OS}&pageNo=1&numOfRows={NUM_OF_ROWS}&listYN=Y" +
            "&&arrange=A&contentTypeId={CONTENT_TYPE_ID}&keyword={KEYWORD}&_type=json",
    )
    fun searchTourismKeywordTemp(
        @PathVariable(name = "SERVICE_KEY") serviceKey: String,
        @PathVariable(name = "MOBILE_APP") mobileApp: String,
        @PathVariable(name = "MOBILE_OS") mobileOs: String,
        @PathVariable(name = "NUM_OF_ROWS") numOfRows: Int,
        @PathVariable(name = "KEYWORD") keyword: String,
        @PathVariable(name = "CONTENT_TYPE_ID") contentTypeId: Int = 15,
    ): String
}
