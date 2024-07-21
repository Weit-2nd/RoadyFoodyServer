package kr.weit.roadyfoody.global.aop

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.global.annotation.DistributedLock
import kr.weit.roadyfoody.support.annotation.ServiceIntegrateTest
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@ServiceIntegrateTest
class DistributedAopTest(
    private val coinService: CoinService,
) : BehaviorSpec({
        given("코인 감소 기능에서") {
            `when`("동시에 코인을 감소시키면") {
                val numberOfThreads = 50

                val executor = Executors.newFixedThreadPool(numberOfThreads)
                val latch = CountDownLatch(numberOfThreads)
                val initialCoin = coinService.coin
                val expectedCoin = initialCoin - numberOfThreads

                repeat(numberOfThreads) {
                    executor.submit {
                        try {
                            coinService.decreaseCoin(1L)
                        } finally {
                            latch.countDown()
                        }
                    }
                }
                latch.await()
                executor.shutdown()
                then("코인이 90개 남는다.") {
                    coinService.coin shouldBe expectedCoin
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
