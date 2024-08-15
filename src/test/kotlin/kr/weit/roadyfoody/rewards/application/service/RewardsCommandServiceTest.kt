package kr.weit.roadyfoody.rewards.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.rewards.fixture.createTestRewards
import kr.weit.roadyfoody.rewards.repository.RewardsRepository
import kr.weit.roadyfoody.user.fixture.createTestUser

class RewardsCommandServiceTest :
    BehaviorSpec(
        {
            val rewardsRepository = mockk<RewardsRepository>()
            val rewardsCommandService = RewardsCommandService(rewardsRepository)

            afterEach { clearAllMocks() }

            given("createRewards 테스트") {
                val rewards = createTestRewards()

                `when`("정상적인 데이터가 들어온 경우") {
                    every { rewardsRepository.save(rewards) } returns rewards
                    then("정상적으로 저장된다.") {
                        rewardsCommandService.createRewards(rewards)
                    }
                }
            }

            given("deleteAllUserRewards 테스트") {
                val user = createTestUser()
                every { rewardsRepository.deleteAllByUser(user) } returns Unit
                `when`("회원 데이터가 들어온 경우") {
                    then("정상적으로 삭제된다.") {
                        rewardsCommandService.deleteAllUserRewards(user)
                        verify(exactly = 1) {
                            rewardsRepository.deleteAllByUser(any())
                        }
                    }
                }
            }
        },
    )
