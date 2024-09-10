package kr.weit.roadyfoody.review.repository

import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.ReviewLike
import kr.weit.roadyfoody.review.domain.ReviewLikeId
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

fun ReviewLikeRepository.getLikedReviewByUser(user: User): List<FoodSpotsReview> = findByUser(user).map { it.review }

@Repository
interface ReviewLikeRepository : JpaRepository<ReviewLike, ReviewLikeId> {
    fun findByUser(user: User): List<ReviewLike>

    fun deleteByUser(user: User)
}
