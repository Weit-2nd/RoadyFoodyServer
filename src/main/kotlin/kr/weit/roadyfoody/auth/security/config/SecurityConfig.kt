package kr.weit.roadyfoody.auth.security.config

import kr.weit.roadyfoody.auth.security.filter.MockPassFilter
import kr.weit.roadyfoody.auth.security.handler.CustomAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(MockPassFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests {
                it
                    .requestMatchers(*PERMITTED_URL_PATTERNS)
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.exceptionHandling {
                it.authenticationEntryPoint(customAuthenticationEntryPoint)
            }.build()
}

private val PERMITTED_URL_PATTERNS =
    arrayOf(
        "/health",
        "/ready",
        "/api/v1/test/success",
        "/api/v1/test/error",
        "/api/v1/test/rest",
        "/api/v1/tourism/**",
        "/api/v1/address/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/actuator/prometheus",
        "/api/v1/terms/**",
        "/api/v1/auth/**",
        "/api/v1/food-spots/**",
    )
