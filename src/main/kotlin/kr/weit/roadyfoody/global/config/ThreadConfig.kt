package kr.weit.roadyfoody.global.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading
import org.springframework.boot.autoconfigure.thread.Threading
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class ThreadConfig {
    private val log: Logger = LoggerFactory.getLogger(ThreadConfig::class.java)

    @Bean
    @ConditionalOnThreading(Threading.VIRTUAL)
    fun virtualThreadExecutor(): ExecutorService {
        log.info("Create virtual executor")
        return Executors.newVirtualThreadPerTaskExecutor()
    }
}
