package kr.weit.roadyfoody.common.annotation

import org.springframework.stereotype.Component
import org.springframework.web.service.annotation.HttpExchange

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
@HttpExchange
annotation class ClientInterface
