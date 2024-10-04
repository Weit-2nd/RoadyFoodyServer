package kr.weit.roadyfoody.support.config.testcontainers

import com.redis.testcontainers.RedisContainer
import org.opensearch.testcontainers.OpensearchContainer
import org.slf4j.LoggerFactory
import org.springframework.boot.sql.init.DatabaseInitializationMode
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

/**
 * testcontainers는 parallel test를 지원하지 않습니다. sequential test로 돌려야 합니다.
 * 또한 왜 에러나는것처럼 보이는지 모르겠는데 실행에는 문제가 없으니 무시해주세요
 */
@TestConfiguration
class TestContainersConfig {
    companion object {
        private const val ORACLE_SID = "xe"
        private const val ORACLE_USER = "system"
        private const val ORACLE_PWD = "oracle"

        private const val ORACLE_PORT = 1521
        private const val REDIS_PORT = 6379
        private const val S3_PORT = 4566
        private const val OPENSEARCH_PORT = 9200

        private val LOGGER = LoggerFactory.getLogger("[TC]")

        // 테스트 컨텍스트가 변하면 sql 초기화를 다시 시도하기 때문에 이를 방지하기 위한 플래그
        private val isSQLInit = AtomicBoolean(false)

        @JvmStatic
        @Container
        val oracleContainer: GenericContainer<*> =
            GenericContainer(DockerImageName.parse("konempty/oracle-db-19c:latest"))
                .withLogConsumer(Slf4jLogConsumer(LOGGER))
                .withExposedPorts(ORACLE_PORT)
                .withReuse(true)
                .waitingFor(Wait.forLogMessage(".*DATABASE IS READY TO USE!.*", 1))

        @JvmStatic
        @Container
        val redisContainer =
            RedisContainer(DockerImageName.parse("redis:7.2.5-alpine3.20"))
                .withLogConsumer(Slf4jLogConsumer(LOGGER))
                .withExposedPorts(REDIS_PORT)
                .withReuse(true)

        @JvmStatic
        @Container
        val s3Container =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:3.5.0"))
                .withLogConsumer(Slf4jLogConsumer(LOGGER))
                .withServices(LocalStackContainer.Service.S3)
                .withExposedPorts(S3_PORT)
                .withReuse(true)

        @JvmStatic
        @Container
        val opensearchContainer =
            OpensearchContainer(DockerImageName.parse("opensearchproject/opensearch:2.14.0"))
                .withLogConsumer(Slf4jLogConsumer(LOGGER))
                .withReuse(true)
                .withExposedPorts(OPENSEARCH_PORT)
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            // 컨테이너를 동시에 다 띄우도록 비동기로 요청을 보내고 뜰때까지 기다린다.
            CompletableFuture
                .allOf(
                    CompletableFuture.runAsync { oracleContainer.start() },
                    CompletableFuture.runAsync { redisContainer.start() },
                    CompletableFuture.runAsync { s3Container.start() },
                    CompletableFuture.runAsync { opensearchContainer.start() },
                ).get()

            val properties =
                mapOf(
                    "spring.datasource.url" to "jdbc:oracle:thin:@//" + oracleContainer.host + ":" +
                        oracleContainer.getMappedPort(ORACLE_PORT) +
                        "/" + ORACLE_SID,
                    "spring.datasource.username" to ORACLE_USER,
                    "spring.datasource.password" to ORACLE_PWD,
                    "spring.sql.init.mode" to
                        (if (isSQLInit.get()) DatabaseInitializationMode.NEVER else DatabaseInitializationMode.ALWAYS).toString(),
                    "spring.data.redis.host" to redisContainer.host,
                    "spring.data.redis.port" to redisContainer.getMappedPort(REDIS_PORT).toString(),
                    "spring.cloud.aws.s3.endpoint" to s3Container.endpoint.toString(),
                    "spring.cloud.aws.s3.bucket" to "test-bucket",
                    "spring.cloud.aws.region.static" to s3Container.region,
                    "spring.cloud.aws.credentials.accessKey" to s3Container.accessKey,
                    "spring.cloud.aws.credentials.secretKey" to s3Container.secretKey,
                    "spring.opensearch.username" to opensearchContainer.username,
                    "spring.opensearch.uri" to opensearchContainer.host,
                    "spring.opensearch.password" to opensearchContainer.password,
                    "spring.opensearch.port" to
                        opensearchContainer
                            .getMappedPort(OPENSEARCH_PORT)
                            .toString(),
                    "spring.opensearch.scheme" to "http",
                )
            isSQLInit.set(true)

            TestPropertyValues.of(properties).applyTo(configurableApplicationContext.environment)
        }
    }
}
