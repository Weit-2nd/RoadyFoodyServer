package kr.weit.roadyfoody.review.domain

import kr.weit.roadyfoody.user.domain.User
import java.io.Serializable

data class ReviewLikeId(
    val review: FoodSpotsReview,
    val user: User,
) : Serializable {
    private constructor() : this(review = FoodSpotsReview(), user = User.of("", "defaultNickname"))
}
