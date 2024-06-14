package kr.weit.roadyfoody.tourism.presentation.client

import kr.weit.roadyfoody.common.annotation.ClientInterface
import kr.weit.roadyfoody.tourism.dto.ResponseWrapper
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange

@ClientInterface
interface TourismClientInterface {
    // /searchKeyword1?serviceKey=RNWgs15JIYmz1aIBG4bJLG2Uu2VcZ7bdR34dbxah1cpG+pJrx3wQJyoMJmi29LNRQxO8Ac3pCgjDX3QOCBpXrw==&MobileApp=AppTest&MobileOS=ETC&pageNo=1&numOfRows=10&listYN=Y&&arrange=A&contentTypeId=15&keyword=강원
    @GetExchange(
        "/searchKeyword1?" +
            "serviceKey={SERVICE_KEY}" +
            "&MobileApp={MOBILE_APP}&MobileOS={MOBILE_OS}&pageNo={PAGE_NO}&numOfRows={NUM_OF_ROWS}&listYN=Y" +
            "&&arrange=A&contentTypeId=15&keyword={KEYWORD}&_type=json",
    )
    fun searchTourismKeyword(
        @PathVariable(name = "SERVICE_KEY") serviceKey: String,
        @PathVariable(name = "MOBILE_APP") mobileApp: String,
        @PathVariable(name = "MOBILE_OS") mobileOs: String,
        @PathVariable(name = "PAGE_NO") pageNo: Int,
        @PathVariable(name = "NUM_OF_ROWS") numOfRows: Int,
        @PathVariable(name = "KEYWORD") keyword: String,
    ): ResponseWrapper
}
