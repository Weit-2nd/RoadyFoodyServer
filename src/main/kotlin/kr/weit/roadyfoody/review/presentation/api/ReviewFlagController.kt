package kr.weit.roadyfoody.review.presentation.api

import jakarta.validation.constraints.Positive
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.review.application.service.ReviewFlagCommandService
import kr.weit.roadyfoody.review.presentation.spec.ReviewFlagControllerSpec
import kr.weit.roadyfoody.user.domain.User
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reviews/{reviewId}/flag")
class ReviewFlagController(
    private val reviewFlagCommandService: ReviewFlagCommandService,
) : ReviewFlagControllerSpec {
    @ResponseStatus(CREATED)
    @PostMapping
    override fun flagReview(
        @LoginUser
        user: User,
        @Positive(message = "리뷰 ID는 양수여야 합니다.")
        @PathVariable("reviewId") reviewId: Long,
    ) {
        reviewFlagCommandService.flagReview(user, reviewId)
    }
}
