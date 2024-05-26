package kr.weit.roadyfoody.testcontainers

import org.slf4j.LoggerFactory
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.OracleContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.DockerImageName

/**
 * testcontainers는 parallel test를 지원하지 않습니다. sequential test로 돌려야 합니다.
 * 또한 왜 에러나는것처럼 보이는지 모르겠는데 실행에는 문제가 없으니 무시해주세요
 */
interface OracleTestContainer {
    companion object {
        private val LOGGER = LoggerFactory.getLogger("[TC_REDIS]")

        @JvmStatic
        val oracleTestContainer =
            OracleContainer(DockerImageName.parse("konempty/oracle-db-19c:arm641").asCompatibleSubstituteFor("gvenzl/oracle-xe")).apply {
                withLogConsumer(Slf4jLogConsumer(LOGGER))
                withDatabaseName("integration-tests-db")
                withUsername("sa")
                withPassword("sa")
                withInitScript("testcontainers/init.sql")
                start()
            }

        @DynamicPropertySource
        @JvmStatic
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", oracleTestContainer::getJdbcUrl)
            registry.add("spring.datasource.password") { "sa" }
            registry.add("spring.datasource.username") { "sa" }
        }
    }
}
