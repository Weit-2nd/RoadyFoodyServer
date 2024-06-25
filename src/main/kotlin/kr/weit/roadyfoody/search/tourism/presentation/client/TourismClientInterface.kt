package kr.weit.roadyfoody.search.tourism.presentation.client

import kr.weit.roadyfoody.common.annotation.ClientInterface
import kr.weit.roadyfoody.search.tourism.dto.ResponseWrapper
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@ClientInterface
interface TourismClientInterface {
    @GetExchange("/searchKeyword1")
    fun searchTourismKeyword(
        @RequestParam(name = "serviceKey") serviceKey: String,
        @RequestParam(name = "MobileApp") mobileApp: String,
        @RequestParam(name = "MobileOS") mobileOs: String,
        @RequestParam(name = "numOfRows") numOfRows: Int,
        @RequestParam(name = "keyword") keyword: String,
        @RequestParam(name = "contentTypeId") contentTypeId: Int = 15,
        @RequestParam(name = "_type") type: String = "json",
        @RequestParam(name = "pageNo") pageNo: Int = 1,
        @RequestParam(name = "listYN") listYN: String = "Y",
        @RequestParam(name = "arrange") arrange: String = "C",
    ): ResponseWrapper
}
