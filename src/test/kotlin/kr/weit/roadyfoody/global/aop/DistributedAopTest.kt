package kr.weit.roadyfoody.global.aop

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.weit.roadyfoody.global.annotation.DistributedLock
import kr.weit.roadyfoody.support.annotation.ServiceIntegrateTest
import org.springframework.stereotype.Service

@ServiceIntegrateTest
class DistributedAopTest(
    private val coinService: CoinService,
) : BehaviorSpec({
        given("코인 감소 기능에서") {
            `when`("동시에 코인을 감소시키면") {
                val numberOfCoroutines = 10
                val initialCoin = 100
                val expectedCoin = initialCoin - numberOfCoroutines

                beforeEach {
                    // 초기 코인 값 설정
                    coinService.coin = initialCoin
                }
                then("코인이 90개 남는다.") {
                    runBlocking {
                        val jobs =
                            List(numberOfCoroutines) {
                                launch {
                                    coinService.decreaseCoin(1L)
                                }
                            }

                        jobs.forEach { it.join() }
                        coinService.coin shouldBe expectedCoin
                    }
                }
            }
        }
    }) {
    @Service
    class CoinService(
        var coin: Int = 100,
    ) {
        @DistributedLock(
            lockName = "COIN-LOCK",
            identifier = "id",
        )
        fun decreaseCoin(id: Long) {
            coin -= 1
        }
    }
}
