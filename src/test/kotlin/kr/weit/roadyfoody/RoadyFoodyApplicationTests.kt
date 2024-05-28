package kr.weit.roadyfoody

import kr.weit.roadyfoody.testcontainers.TestContainersConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ContextConfiguration(initializers = [TestContainersConfig.Initializer::class])
@SpringBootTest
class RoadyFoodyApplicationTests {
    @Test
    fun contextLoads() {
    }
}
