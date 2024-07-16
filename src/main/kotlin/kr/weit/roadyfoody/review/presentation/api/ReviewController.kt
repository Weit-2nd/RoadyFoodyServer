package kr.weit.roadyfoody.review.presentation.api

import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.foodSpots.validator.WebPImageList
import kr.weit.roadyfoody.review.application.dto.ReviewRequest
import kr.weit.roadyfoody.review.application.service.ReviewCommandService
import kr.weit.roadyfoody.review.presentation.spec.ReviewControllerSpec
import kr.weit.roadyfoody.user.domain.User
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/review")
class ReviewController(
    private val reviewCommandService: ReviewCommandService,
) : ReviewControllerSpec {
    @ResponseStatus(CREATED)
    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    override fun createReview(
        @LoginUser
        user: User,
        @Valid
        @RequestPart
        reviewRequest: ReviewRequest,
        @Size(max = 3, message = "이미지는 최대 3개까지 업로드할 수 있습니다.")
        @WebPImageList
        @RequestPart(required = false)
        reviewPhotos: List<MultipartFile>?,
    ) {
        reviewCommandService.createReview(user, reviewRequest, reviewPhotos)
    }
}
