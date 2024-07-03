package kr.weit.roadyfoody.support.annotation

import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import kr.weit.roadyfoody.global.config.JpaAuditingConfig
import kr.weit.roadyfoody.testcontainers.TestContainersConfig
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@Testcontainers
@ContextConfiguration(initializers = [TestContainersConfig.Initializer::class])
@Import(JpaAuditingConfig::class, KotlinJdslAutoConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
annotation class RepositoryTest
