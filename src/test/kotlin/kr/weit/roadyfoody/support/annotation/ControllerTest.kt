package kr.weit.roadyfoody.support.annotation

import kr.weit.roadyfoody.auth.security.config.SecurityConfig
import kr.weit.roadyfoody.auth.security.handler.CustomAuthenticationEntryPoint
import kr.weit.roadyfoody.global.log.TraceManager
import kr.weit.roadyfoody.global.utils.ObjectMapperProvider
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@Import(TraceManager::class, SecurityConfig::class, ObjectMapperProvider::class, CustomAuthenticationEntryPoint::class)
annotation class ControllerTest
