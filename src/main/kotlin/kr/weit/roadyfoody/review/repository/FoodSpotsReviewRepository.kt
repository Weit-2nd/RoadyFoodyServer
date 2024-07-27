package kr.weit.roadyfoody.review.repository

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import kr.weit.roadyfoody.global.utils.getSlice
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.exception.ReviewNotFoundException
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

fun FoodSpotsReviewRepository.getByReview(id: Long): FoodSpotsReview = findById(id).orElseThrow { ReviewNotFoundException() }

interface FoodSpotsReviewRepository :
    JpaRepository<FoodSpotsReview, Long>,
    CustomFoodSpotsReviewRepository {
    fun findByUser(user: User): List<FoodSpotsReview>
}

interface CustomFoodSpotsReviewRepository {
    fun sliceByUser(
        user: User,
        size: Int,
        lastId: Long?,
    ): Slice<FoodSpotsReview>

    fun sliceByFoodSpots(
        foodSpotsId: Long,
        size: Int,
        lastId: Long?,
        sortType: ReviewSortType,
    ): Slice<FoodSpotsReview>
}

class CustomFoodSpotsReviewRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : CustomFoodSpotsReviewRepository {
    override fun sliceByUser(
        user: User,
        size: Int,
        lastId: Long?,
    ): Slice<FoodSpotsReview> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(FoodSpotsReview::class))
                .from(entity(FoodSpotsReview::class))
                .whereAnd(
                    if (lastId != null) {
                        path(FoodSpotsReview::id).lessThan(lastId)
                    } else {
                        null
                    },
                    path(FoodSpotsReview::user).equal(user),
                ).orderBy(path(FoodSpotsReview::id).desc())
        }
    }

    override fun sliceByFoodSpots(
        foodSpotsId: Long,
        size: Int,
        lastId: Long?,
        sortType: ReviewSortType,
    ): Slice<FoodSpotsReview> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(FoodSpotsReview::class))
                .from(entity(FoodSpotsReview::class))
                .whereAnd(
                    if (lastId != null) {
                        when (sortType) {
                            ReviewSortType.LATEST -> path(FoodSpotsReview::id).lessThan(lastId)
                            ReviewSortType.HIGHEST -> {
                                val rate = getRateByLastId(lastId)
                                or(
                                    and(
                                        path(FoodSpotsReview::rate).equal(rate),
                                        path(FoodSpotsReview::id).lessThan(lastId),
                                    ),
                                    path(FoodSpotsReview::rate).lessThan(rate),
                                )
                            }
                        }
                    } else {
                        null
                    },
                ).orderBy(
                    when (sortType) {
                        ReviewSortType.LATEST -> path(FoodSpotsReview::user).desc()
                        ReviewSortType.HIGHEST -> {
                            path(FoodSpotsReview::rate).desc()
                            path(FoodSpotsReview::id).desc()
                        }
                    },
                )
        }
    }

    private fun getRateByLastId(lastId: Long): Int =
        kotlinJdslJpqlExecutor
            .findAll {
                select(path(FoodSpotsReview::rate))
                    .from(entity(FoodSpotsReview::class))
                    .where(path(FoodSpotsReview::id).equal(lastId))
            }.single()!!
}

enum class ReviewSortType {
    LATEST,
    HIGHEST,
}
