package kr.weit.roadyfoody.ranking.presentation.api

import jakarta.validation.constraints.Positive
import kr.weit.roadyfoody.foodSpots.application.dto.UserReportCount
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService
import kr.weit.roadyfoody.ranking.presentation.spec.RankingControllerSpec
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ranking")
class RankingController(
    private val foodSpotsCommandService: FoodSpotsCommandService,
) : RankingControllerSpec {
    @GetMapping("/report")
    override fun getReportRanking(
        @Positive(message = "size는 양수여야 합니다.")
        @RequestParam(defaultValue = "10")
        size: Long,
    ): List<UserReportCount> = foodSpotsCommandService.getReportRanking(size)
}
