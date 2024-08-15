package kr.weit.roadyfoody.rewards.fixture

import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodHistory
import kr.weit.roadyfoody.global.TEST_PAGE_SIZE
import kr.weit.roadyfoody.rewards.application.dto.RewardsResponse
import kr.weit.roadyfoody.rewards.domain.RewardType
import kr.weit.roadyfoody.rewards.domain.Rewards
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import java.time.LocalDateTime

fun createRewardsResponse(): SliceResponse<RewardsResponse> =
    SliceResponse(
        contents =
            listOf(
                RewardsResponse(
                    id = 0L,
                    coinReceived = "true",
                    rewardPoint = 100,
                    rewardType = "리포트 업데이트",
                    createdAt = LocalDateTime.now(),
                ),
            ),
        hasNext = false,
    )

class MockTestRewards(
    id: Long = 0L,
    user: User = createTestUser(),
    foodSpotsHistory: FoodSpotsHistory? = createTestFoodHistory(),
    rewardPoint: Int = 100,
    coinReceived: Boolean = true,
    rewardType: RewardType = RewardType.REPORT_CREATE,
) : Rewards(
        id = id,
        user = user,
        foodSpotsHistory = foodSpotsHistory,
        rewardPoint = rewardPoint,
        coinReceived = coinReceived,
        rewardType = rewardType,
    ) {
    override var createdDateTime: LocalDateTime = LocalDateTime.now()
}

fun createTestRewards(
    user: User = createTestUser(0L),
    foodSpotsHistory: FoodSpotsHistory = createTestFoodHistory(0L),
    rewardPoint: Int = 100,
    coinReceived: Boolean = true,
    rewardType: RewardType = RewardType.REPORT_CREATE,
): Rewards =
    Rewards(
        user = user,
        foodSpotsHistory = foodSpotsHistory,
        rewardPoint = rewardPoint,
        coinReceived = coinReceived,
        rewardType = rewardType,
    )

fun createSliceRewards(): Slice<Rewards> =
    SliceImpl(
        listOf(MockTestRewards()),
        Pageable.ofSize(TEST_PAGE_SIZE),
        false,
    )
