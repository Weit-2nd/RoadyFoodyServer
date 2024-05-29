package kr.weit.roadyfoody.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApiDocsConfig {
    @Bean
    fun publicApi(): GroupedOpenApi {
        // pathsToMatch로 원하는 경로의 api만 나오도록 설정
        return GroupedOpenApi.builder()
            .group("API")
            .pathsToMatch("/api/**")
            .build()
    }

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("로디푸디 API")
                    .description("로디푸디 API 문서입니다.")
                    .version("1.0.0"),
            )
    }
}
