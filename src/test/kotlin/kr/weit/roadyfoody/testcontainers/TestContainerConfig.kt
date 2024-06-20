package kr.weit.roadyfoody.testcontainers

import com.redis.testcontainers.RedisContainer
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
        private val LOGGER = LoggerFactory.getLogger("[TC]")

        // 테스트 컨텍스트가 변하면 sql 초기화를 다시 시도하기 때문에 이를 방지하기 위한 플래그
        private val isSQLInit = AtomicBoolean(false)

        @JvmStatic
        @Container
        val oracleContainer: GenericContainer<*> =
            GenericContainer("konempty/oracle-db-19c:latest")
                .withLogConsumer(Slf4jLogConsumer(LOGGER))
                .withExposedPorts(1521)
                .withReuse(true)
                .waitingFor(Wait.forLogMessage(".*DATABASE IS READY TO USE!.*", 1))

        @JvmStatic
        @Container
        val redisContainer =
            RedisContainer(
                "redis:7.2.5-alpine3.20",
            ).apply {
                withLogConsumer(Slf4jLogConsumer(LOGGER))
                withExposedPorts(6379)
                withReuse(true)
            }

        @JvmStatic
        @Container
        val s3Container =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:3.5.0"))
                .withServices(LocalStackContainer.Service.S3)
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            // 컨테이너를 동시에 다 띄우도록 비동기로 요청을 보내고 뜰때까지 기다린다.
            CompletableFuture
                .allOf(
                    CompletableFuture.runAsync { oracleContainer.start() },
                    CompletableFuture.runAsync { redisContainer.start() },
                    CompletableFuture.runAsync { s3Container.start() },
                ).get()

            val properties =
                mapOf(
                    "spring.datasource.url" to "jdbc:oracle:thin:@//" + oracleContainer.host + ":" +
                        oracleContainer.getMappedPort(1521) +
                        "/" + ORACLE_SID,
                    "spring.datasource.username" to ORACLE_USER,
                    "spring.datasource.password" to ORACLE_PWD,
                    "spring.sql.init.mode" to
                        (if (isSQLInit.get()) DatabaseInitializationMode.NEVER else DatabaseInitializationMode.ALWAYS).toString(),
                    "spring.data.redis.host" to redisContainer.host,
                    "spring.data.redis.port" to redisContainer.getMappedPort(6379).toString(),
                    "aws.s3.endpoint" to s3Container.endpoint.toString(),
                    "aws.s3.region" to s3Container.region,
                    "aws.s3.access-key" to s3Container.accessKey,
                    "aws.s3.secret-key" to s3Container.secretKey,
                    "aws.s3.bucket-name" to "test-bucket",
                )
            isSQLInit.set(true)

            TestPropertyValues.of(properties).applyTo(configurableApplicationContext.environment)
        }
    }
}
