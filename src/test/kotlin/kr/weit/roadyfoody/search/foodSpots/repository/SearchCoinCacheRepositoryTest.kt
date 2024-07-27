package kr.weit.roadyfoody.search.foodSpots.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.fixture.createMockSearchCoinCaches
import kr.weit.roadyfoody.search.foodSpots.domain.SearchCoinCache
import kr.weit.roadyfoody.support.config.testcontainers.TestContainersConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("test")
@Testcontainers
@ContextConfiguration(initializers = [TestContainersConfig.Initializer::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
class SearchCoinCacheRepositoryTest
    @Autowired
    constructor(
        private val searchCoinCacheRepository: SearchCoinCacheRepository,
    ) : DescribeSpec({
            describe("findSearchCoinCachesByUserId 메소드는") {
                context("존재하는 user id 를 받는 경우") {
                    it("해당 user 의 SearchCoinCache 리스트를 반환한다.") {
                        val searchCoinCaches =
                            searchCoinCacheRepository.saveAll(
                                createMockSearchCoinCaches(0L),
                            ) as List<SearchCoinCache>
                        val result = searchCoinCacheRepository.findByUserId(0L)
                        result shouldHaveSize searchCoinCaches.size
                        result.forEachIndexed { index, cache ->
                            cache.id shouldBe searchCoinCaches[index].id
                        }
                    }
                }
            }
        })
