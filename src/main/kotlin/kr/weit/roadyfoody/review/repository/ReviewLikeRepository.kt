package kr.weit.roadyfoody.review.repository

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import kr.weit.roadyfoody.global.utils.getSlice
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.ReviewLike
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

fun ReviewLikeRepository.getLikedReviewByUser(user: User): List<FoodSpotsReview> = findByUser(user).map { it.review }

@Repository
interface ReviewLikeRepository :
    JpaRepository<ReviewLike, Long>,
    CustomReviewLikeRepository {
    fun existsByReviewAndUser(
        review: FoodSpotsReview,
        user: User,
    ): Boolean

    fun findByUser(user: User): List<ReviewLike>

    fun deleteByUser(user: User)

    fun deleteByReviewAndUser(
        review: FoodSpotsReview,
        user: User,
    )
}

interface CustomReviewLikeRepository {
    fun sliceLikeReviews(
        user: User,
        size: Int,
        lastId: Long?,
    ): Slice<ReviewLike>
}

class CustomReviewLikeRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : CustomReviewLikeRepository {
    override fun sliceLikeReviews(
        user: User,
        size: Int,
        lastId: Long?,
    ): Slice<ReviewLike> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor
            .getSlice(pageable) {
                select(entity(ReviewLike::class))
                    .from(entity(ReviewLike::class))
                    .whereAnd(
                        if (lastId != null) {
                            path(ReviewLike::id).lessThan(lastId)
                        } else {
                            null
                        },
                        path(ReviewLike::user).equal(user),
                    ).orderBy(path(ReviewLike::id).desc())
            }
    }
}
