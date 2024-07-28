package kr.weit.roadyfoody.search.address.presentation.client

import kr.weit.roadyfoody.global.annotation.ClientInterface
import kr.weit.roadyfoody.search.address.dto.Point2AddressWrapper
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@ClientInterface
interface KakaoPointClientInterface {
    @GetExchange("/v2/local/geo/coord2address.json")
    fun searchPointToAddress(
        @RequestParam(name = "x") longitude: String,
        @RequestParam(name = "y") latitude: String,
    ): Point2AddressWrapper
}
