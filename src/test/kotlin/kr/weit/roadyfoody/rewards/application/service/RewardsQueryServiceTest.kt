package kr.weit.roadyfoody.rewards.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.rewards.fixture.createSliceRewards
import kr.weit.roadyfoody.rewards.fixture.createTestRewards
import kr.weit.roadyfoody.rewards.repository.RewardsRepository
import org.springframework.data.domain.PageRequest

class RewardsQueryServiceTest :
    BehaviorSpec(
        {
            val rewardsRepository = mockk<RewardsRepository>()
            val rewardsQueryService = RewardsQueryService(rewardsRepository)

            afterEach { clearAllMocks() }

            given("getUserRewards 테스트") {
                val rewards = createTestRewards()
                val pageable = PageRequest.of(0, 10)

                `when`("정상적인 데이터가 들어온 경우") {
                    every {
                        rewardsRepository.findAllByUser(any(), any())
                    } returns createSliceRewards()
                    then("정상적으로 저장된다."){
                        val userRewards = rewardsQueryService.getUserRewards(rewards.user, pageable)
                        userRewards.contents.size shouldBe 1
                    }
                }
            }
        }
    )