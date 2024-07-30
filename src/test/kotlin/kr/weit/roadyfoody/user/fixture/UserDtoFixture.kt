package kr.weit.roadyfoody.user.fixture

import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_PHOTO_URL
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodHistory
import kr.weit.roadyfoody.user.application.dto.UserInfoResponse
import kr.weit.roadyfoody.user.application.dto.UserReportCategoryResponse
import kr.weit.roadyfoody.user.application.dto.UserReportHistoriesResponse
import kr.weit.roadyfoody.user.application.dto.UserReportPhotoResponse

fun createTestUserInfoResponse(
    nickname: String = TEST_USER_NICKNAME,
    profileImageUrl: String = TEST_USER_PROFILE_IMAGE_URL,
    coin: Int = TEST_USER_COIN,
) = UserInfoResponse(
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    coin = coin,
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
