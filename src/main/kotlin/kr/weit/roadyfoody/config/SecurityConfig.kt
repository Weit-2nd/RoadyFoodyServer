package kr.weit.roadyfoody.config

import kr.weit.roadyfoody.security.filter.MockPassFilter
import kr.weit.roadyfoody.security.handler.CustomAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    val mockPassFilter: MockPassFilter,
    val customAuthenticationFilter: CustomAuthenticationEntryPoint,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(mockPassFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests {
                it
                    .requestMatchers(*PERMITTED_URL_PATTERNS).permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling {
                it.authenticationEntryPoint(customAuthenticationFilter)
            }
            .build()
    }
}

private val PERMITTED_URL_PATTERNS =
    arrayOf(
        "/health",
        "/ready",
        "/api/v1/test/success",
        "/api/v1/test/error",
        "/swagger-ui/**",
        "/api-docs/**",
    )
