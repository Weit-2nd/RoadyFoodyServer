package kr.weit.roadyfoody.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig {
    @Bean(name = ["asyncTask"])
    fun threadPoolTaskExecutor(): Executor {
        val taskExecutor = ThreadPoolTaskExecutor()
        taskExecutor.corePoolSize = CORE_POOL_SIZE
        taskExecutor.maxPoolSize = MAX_POOL_SIZE
        taskExecutor.queueCapacity = QUEUE_CAPACITY
        taskExecutor.setThreadNamePrefix("async-thread-")
        return taskExecutor
    }

    companion object {
        private const val CORE_POOL_SIZE = 5
        private const val MAX_POOL_SIZE = 10
        private const val QUEUE_CAPACITY = 30
    }
}
