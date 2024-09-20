package kr.weit.roadyfoody.user.presentation.api

import jakarta.validation.Valid
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Positive
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.global.validator.MaxFileSize
import kr.weit.roadyfoody.global.validator.WebPImage
import kr.weit.roadyfoody.user.application.dto.UserInfoResponse
import kr.weit.roadyfoody.user.application.dto.UserLikedReviewResponse
import kr.weit.roadyfoody.user.application.dto.UserNicknameRequest
import kr.weit.roadyfoody.user.application.dto.UserReportHistoriesResponse
import kr.weit.roadyfoody.user.application.dto.UserReviewResponse
import kr.weit.roadyfoody.user.application.dto.UserStatisticsResponse
import kr.weit.roadyfoody.user.application.service.UserCommandService
import kr.weit.roadyfoody.user.application.service.UserQueryService
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.presentation.spec.UserControllerSpec
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userQueryService: UserQueryService,
    private val userCommandService: UserCommandService,
) : UserControllerSpec {
    @GetMapping("/me")
    override fun getLoginUserInfo(
        @LoginUser user: User,
    ): UserInfoResponse = userQueryService.getUserInfo(user)

    @GetMapping("{userId}/food-spots/histories")
    override fun getReportHistories(
        @PathVariable("userId")
        userId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @Positive(message = "마지막 ID는 양수여야 합니다.")
        @RequestParam(required = false)
        lastId: Long?,
    ): SliceResponse<UserReportHistoriesResponse> = userQueryService.getReportHistories(userId, size, lastId)

    @GetMapping("{userId}/reviews")
    override fun getUserReviews(
        @PathVariable("userId")
        @Positive(message = "유저 ID는 양수여야 합니다.")
        userId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @Positive(message = "마지막 ID는 양수여야 합니다.")
        @RequestParam(required = false)
        lastId: Long?,
    ): SliceResponse<UserReviewResponse> = userQueryService.getUserReviews(userId, size, lastId)

    @GetMapping("{userId}/likes/reviews")
    override fun getUserLikeReviews(
        @PathVariable("userId")
        @Positive(message = "유저 ID는 양수여야 합니다.")
        userId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @Past(message = "마지막 시간은 현재 시간 이전이어야 합니다.")
        @RequestParam(required = false)
        lastTime: LocalDateTime?,
    ): SliceResponse<UserLikedReviewResponse> = userQueryService.getLikeReviews(userId, size, lastTime)

    @GetMapping("{userId}/statistics")
    override fun getUserStatistics(
        @PathVariable("userId")
        @Positive(message = "유저 ID는 양수여야 합니다.")
        userId: Long,
    ): UserStatisticsResponse = userQueryService.getUserStatistics(userId)

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/nickname")
    override fun updateNickname(
        @LoginUser
        user: User,
        @RequestBody
        @Valid
        userNicknameRequest: UserNicknameRequest,
    ) {
        userCommandService.updateNickname(user, userNicknameRequest.nickname)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/profile", consumes = [MULTIPART_FORM_DATA_VALUE])
    override fun updateProfileImage(
        @LoginUser
        user: User,
        @RequestPart
        @MaxFileSize
        @WebPImage
        profileImage: MultipartFile,
    ) {
        userCommandService.updateProfileImage(user, profileImage)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/profile")
    override fun deleteProfile(
        @LoginUser
        user: User,
    ) {
        userCommandService.deleteProfileImage(user)
    }
}
