package kr.weit.roadyfoody.foodSpots.repository

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.exception.FoodSpotsHistoryNotFoundException
import kr.weit.roadyfoody.global.utils.getSlice
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

fun FoodSpotsHistoryRepository.getHistoriesByUser(
    user: User,
    size: Int,
    lastId: Long?,
): Slice<FoodSpotsHistory> = findSliceByUser(user, size, lastId)

fun FoodSpotsHistoryRepository.getByHistoryId(historyId: Long): FoodSpotsHistory =
    findById(historyId).orElseThrow { FoodSpotsHistoryNotFoundException() }

// TODO: 리포트 삭제시, 음식점에 대한 리포트가 존재하지 않으면 가게 삭제 처리 이후에 이곳 에러 처리 추가하기
fun FoodSpotsHistoryRepository.getByFoodSpots(foodSpots: FoodSpots): List<FoodSpotsHistory> = findByFoodSpots(foodSpots)

interface FoodSpotsHistoryRepository :
    JpaRepository<FoodSpotsHistory, Long>,
    CustomFoodSpotsHistoryRepository {
    fun findByUser(user: User): List<FoodSpotsHistory>

    fun findByFoodSpots(foodSpots: FoodSpots): List<FoodSpotsHistory>
}

interface CustomFoodSpotsHistoryRepository {
    fun findSliceByUser(
        user: User,
        size: Int,
        lastId: Long?,
    ): Slice<FoodSpotsHistory>
}

class CustomFoodSpotsHistoryRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : CustomFoodSpotsHistoryRepository {
    override fun findSliceByUser(
        user: User,
        size: Int,
        lastId: Long?,
    ): Slice<FoodSpotsHistory> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(FoodSpotsHistory::class))
                .from(entity(FoodSpotsHistory::class))
                .whereAnd(
                    if (lastId != null) {
                        path(FoodSpotsHistory::id).lessThan(lastId)
                    } else {
                        null
                    },
                    path(FoodSpotsHistory::user).equal(user),
                ).orderBy(path(FoodSpotsHistory::id).desc())
        }
    }
}
