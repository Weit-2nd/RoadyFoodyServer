package kr.weit.roadyfoody.search.address.presentation.client

import kr.weit.roadyfoody.common.annotation.ClientInterface
import kr.weit.roadyfoody.search.address.application.dto.AddressResponseWrapper
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@ClientInterface
interface DapiClientInterface {
    @GetExchange("/v2/local/search/keyword.json")
    fun searchAddress(
        @RequestHeader(name = "Authorization") apiKey: String,
        @RequestParam(name = "query") keyword: String,
        @RequestParam(name = "size") size: Int? = 10,
    ): AddressResponseWrapper
}
