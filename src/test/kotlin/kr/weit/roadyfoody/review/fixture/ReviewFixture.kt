import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.TEST_PHOTO_NAME
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpots
import kr.weit.roadyfoody.global.TEST_PAGE_SIZE
import kr.weit.roadyfoody.review.application.dto.ReviewPhotoResponse
import kr.weit.roadyfoody.review.application.dto.ReviewRequest
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.FoodSpotsReviewPhoto
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import java.time.LocalDateTime

const val TEST_FOOD_SPOT_ID = 1L
const val TEST_REVIEW_CONTENT = "testReview"
const val TEST_REVIEW_RATING = 10
const val TEST_REVIEW_REQUEST_NAME = "reviewRequest"
const val TEST_REVIEW_CONTENT_MAX_LENGTH = 1200
const val TEST_INVALID_FOOD_SPOT_ID = -1L
const val TEST_INVALID_RATING = -1
const val TEST_INVALID_RATING_OVER = 11
const val TEST_REVIEW_REQUEST_PHOTO = "reviewPhotos"
const val TEST_REVIEW_ID = 1L
const val TEST_REVIEW_PHOTO_URL = "reviewPhotoUrl"
const val TEST_REVIEW_PHOTO_ID = 1L

fun createTestReviewRequest(
    foodSpotsId: Long = TEST_FOOD_SPOT_ID,
    contents: String = TEST_REVIEW_CONTENT,
    rating: Int = TEST_REVIEW_RATING,
) = ReviewRequest(foodSpotsId, contents, rating)

fun createTestFoodSpotsReview(
    user: User = createTestUser(),
    foodSpots: FoodSpots = createTestFoodSpots(),
    rate: Int = TEST_REVIEW_RATING,
) = FoodSpotsReview(0L, foodSpots, user, rate, TEST_REVIEW_CONTENT)

fun createTestFoodSpotsReviews(
    user: User = createTestUser(),
    foodSpots: FoodSpots = createTestFoodSpots(),
    rate: Int = TEST_REVIEW_RATING,
    size: Int = 3,
) = List(size) {
    createTestFoodSpotsReview(user, foodSpots, rate)
}

fun createTestFoodSpotsReviews(
    user: User = createTestUser(),
    foodSpots: FoodSpots = createTestFoodSpots(),
    otherRate: Int = 5,
    sizeOfAllReviews: Int = 3,
    sizeOfHighRatedReviews: Int = 1,
) = createTestFoodSpotsReviews(
    user = user,
    foodSpots = foodSpots,
    rate = otherRate,
    size =
        sizeOfAllReviews - sizeOfHighRatedReviews,
) +
    createTestFoodSpotsReviews(
        user = user,
        foodSpots = foodSpots,
        rate = Badge.HIGH_RATING_CONDITION,
        size = sizeOfHighRatedReviews,
    )

fun createMockTestReview(
    user: User = createTestUser(),
    foodSpots: FoodSpots = createTestFoodSpots(),
) = MockTestReview(user = user, foodSpots = foodSpots)

fun createTestReviewPhoto(foodSpotsReview: FoodSpotsReview = createMockTestReview()) =
    FoodSpotsReviewPhoto(0L, foodSpotsReview, TEST_PHOTO_NAME)

fun createMockSliceReview(): Slice<FoodSpotsReview> =
    SliceImpl(
        listOf(createMockTestReview()),
        Pageable.ofSize(TEST_PAGE_SIZE),
        false,
    )

fun createTestReviewPhotoResponse(): ReviewPhotoResponse = ReviewPhotoResponse(TEST_REVIEW_PHOTO_ID, TEST_REVIEW_PHOTO_URL)

class MockTestReview(
    id: Long = 0L,
    foodSpots: FoodSpots = createTestFoodSpots(),
    user: User = createTestUser(),
    contents: String = TEST_REVIEW_CONTENT,
    rating: Int = TEST_REVIEW_RATING,
) : FoodSpotsReview(id, foodSpots, user, rating, contents) {
    override var createdDateTime: LocalDateTime = LocalDateTime.now()
}
