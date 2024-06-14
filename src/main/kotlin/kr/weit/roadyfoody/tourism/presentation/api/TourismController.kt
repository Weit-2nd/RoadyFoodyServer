package kr.weit.roadyfoody.tourism.presentation.api

import kr.weit.roadyfoody.tourism.application.service.TourismService
import kr.weit.roadyfoody.tourism.dto.ResponseWrapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tourism")
class TourismController(
    private val tourismService: TourismService,
) {
    @GetMapping
    fun searchTourismKeyword(
        pageNo: Int,
        numOfRows: Int,
        keyword: String,
    ): ResponseWrapper {
        return tourismService.searchTourism(pageNo, numOfRows, keyword)
    }
}
