package kr.weit.roadyfoody.support.annotation

import kr.weit.roadyfoody.global.testcontainers.TestContainersConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = [TestContainersConfig.Initializer::class])
annotation class ServiceIntegrateTest
