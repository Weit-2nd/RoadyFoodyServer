package kr.weit.roadyfoody.foodSpots.application.service

import com.ninjasquad.springmockk.SpykBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.verify
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.support.annotation.ServiceIntegrateTest

@ServiceIntegrateTest
class FoodSpotsCommandServiceIntegrationTest(
    @SpykBean private val foodSpotsRepository: FoodSpotsRepository,
    private val foodSpotsCommandService: FoodSpotsCommandService,
) : BehaviorSpec(
        {

            given("가게 오픈 상태 업데이트") {
                `when`("동시에 가게 오픈 상태 업데이트 요청이 들어올 경우") {
                    val numberOfCoroutines = 10
                    then("한번만 업데이트 되어야 한다.") {

                        runBlocking {
                            val jobs =
                                List(numberOfCoroutines) {
                                    launch {
                                        foodSpotsCommandService.setFoodSpotsOpen()
                                    }
                                }
                            jobs.joinAll()
                            verify(exactly = 1) { foodSpotsRepository.updateOpeningStatus() }
                        }
                    }
                }
            }
        },
    )
