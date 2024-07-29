package kr.weit.roadyfoody.search.address.presentation.api

import kr.weit.roadyfoody.search.address.application.service.AddressSearchService
import kr.weit.roadyfoody.search.address.dto.AddressSearchResponses
import kr.weit.roadyfoody.search.address.dto.Point2AddressResponse
import kr.weit.roadyfoody.search.address.presentation.spec.AddressSearchControllerSpec
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/address")
class AddressSearchController(
    private val addressSearchService: AddressSearchService,
) : AddressSearchControllerSpec {
    @GetMapping("/search")
    override fun searchAddress(
        numOfRows: Int,
        keyword: String,
    ): AddressSearchResponses = addressSearchService.searchAddress(keyword, numOfRows)

    @GetMapping("/point")
    override fun searchPoint2Address(
        longitude: Double,
        latitude: Double,
    ): Point2AddressResponse =
        addressSearchService.searchPoint2Address(
            longitude.toString(),
            latitude.toString(),
        )
}
