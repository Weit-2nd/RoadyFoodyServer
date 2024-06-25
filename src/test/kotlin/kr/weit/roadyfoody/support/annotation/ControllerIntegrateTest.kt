package kr.weit.roadyfoody.support.annotation

import kr.weit.roadyfoody.testcontainers.TestContainersConfig
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@Testcontainers
@ContextConfiguration(initializers = [TestContainersConfig.Initializer::class])
annotation class ControllerIntegrateTest
