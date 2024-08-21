package kr.weit.roadyfoody.user.fixture

import createMockSliceReview
import createTestReviewPhotoResponse
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_PHOTO_URL
import kr.weit.roadyfoody.foodSpots.fixture.TEST_REST_DAILY_REPORT_CREATION_COUNT
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodHistory
import kr.weit.roadyfoody.user.application.dto.ReviewerInfoResponse
import kr.weit.roadyfoody.user.application.dto.UserInfoResponse
import kr.weit.roadyfoody.user.application.dto.UserReportCategoryResponse
import kr.weit.roadyfoody.user.application.dto.UserReportHistoriesResponse
import kr.weit.roadyfoody.user.application.dto.UserReportPhotoResponse
import kr.weit.roadyfoody.user.application.dto.UserReviewResponse

fun createTestUserInfoResponse(
    nickname: String = TEST_USER_NICKNAME,
    profileImageUrl: String = TEST_USER_PROFILE_IMAGE_URL,
    coin: Int = TEST_USER_COIN,
    restDailyReportCreationCount: Int = TEST_REST_DAILY_REPORT_CREATION_COUNT,
) = UserInfoResponse(
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    coin = coin,
    restDailyReportCreationCount = restDailyReportCreationCount,
)

fun createTestUserReportPhotoResponse(
    id: Long = 0L,
    url: String = TEST_FOOD_SPOTS_PHOTO_URL,
) = UserReportPhotoResponse(id, url)

fun createTestUserReportCategoryResponse(
    id: Long = 0L,
    name: String = "testCategory",
) = UserReportCategoryResponse(id, name)

fun createTestUserReportHistoriesResponse(
    foodSpotsHistory: FoodSpotsHistory = createMockTestFoodHistory(),
    reportPhotoResponse: List<UserReportPhotoResponse> = listOf(createTestUserReportPhotoResponse()),
    reportCategoryResponse: List<UserReportCategoryResponse> = listOf(createTestUserReportCategoryResponse()),
) = UserReportHistoriesResponse(
    foodSpotsHistory,
    reportPhotoResponse,
    reportCategoryResponse,
)

fun createTestSliceResponseUserReview(): SliceResponse<UserReviewResponse> =
    SliceResponse(
        createMockSliceReview().map {
            UserReviewResponse(
                it,
                listOf(createTestReviewPhotoResponse()),
            )
        },
    )

fun createTestReviewerInfoResponse(): ReviewerInfoResponse = ReviewerInfoResponse.of(createTestUser(), TEST_USER_PROFILE_IMAGE_URL)
