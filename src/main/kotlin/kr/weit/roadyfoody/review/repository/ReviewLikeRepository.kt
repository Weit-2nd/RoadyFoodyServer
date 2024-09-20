package kr.weit.roadyfoody.review.repository

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import kr.weit.roadyfoody.global.utils.getSlice
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.ReviewLike
import kr.weit.roadyfoody.review.domain.ReviewLikeId
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

fun ReviewLikeRepository.getLikedReviewByUser(user: User): List<FoodSpotsReview> = findByUser(user).map { it.review }

@Repository
interface ReviewLikeRepository :
    JpaRepository<ReviewLike, ReviewLikeId>,
    CustomReviewLikeRepository {
    fun findByUser(user: User): List<ReviewLike>

    fun deleteByUser(user: User)

    fun countByUser(user: User): Int
}

interface CustomReviewLikeRepository {
    fun sliceLikeReviews(
        user: User,
        size: Int,
        lastTime: LocalDateTime?,
    ): Slice<ReviewLike>
}

class CustomReviewLikeRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : CustomReviewLikeRepository {
    override fun sliceLikeReviews(
        user: User,
        size: Int,
        lastTime: LocalDateTime?,
    ): Slice<ReviewLike> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor
            .getSlice(pageable) {
                select(entity(ReviewLike::class))
                    .from(entity(ReviewLike::class))
                    .whereAnd(
                        if (lastTime != null) {
                            path(ReviewLike::createdDateTime).lessThan(lastTime)
                        } else {
                            null
                        },
                        path(ReviewLike::user).equal(user),
                    ).orderBy(path(ReviewLike::createdDateTime).desc())
            }
    }
}
