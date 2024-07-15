import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.TEST_PHOTO_NAME
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpots
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.FoodSpotsReviewPhoto
import kr.weit.roadyfoody.review.dto.ReviewRequest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
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

fun createTestReviewRequest(
    foodSpotsId: Long = TEST_FOOD_SPOT_ID,
    contents: String = TEST_REVIEW_CONTENT,
    rating: Int = TEST_REVIEW_RATING,
) = ReviewRequest(foodSpotsId, contents, rating)

fun createMockTestReview(
    user: User = createTestUser(),
    foodSpots: FoodSpots = createTestFoodSpots(),
) = MockTestReview(user = user, foodSpots = foodSpots)

fun createTestReviewPhoto(foodSpotsReview: FoodSpotsReview = createMockTestReview()) =
    FoodSpotsReviewPhoto(0L, foodSpotsReview, TEST_PHOTO_NAME)

class MockTestReview(
    id: Long = 0L,
    foodSpots: FoodSpots = createTestFoodSpots(),
    user: User = createTestUser(),
    contents: String = TEST_REVIEW_CONTENT,
    rating: Int = TEST_REVIEW_RATING,
) : FoodSpotsReview(id, foodSpots, user, rating, contents) {
    override var createdDateTime: LocalDateTime = LocalDateTime.now()
}
