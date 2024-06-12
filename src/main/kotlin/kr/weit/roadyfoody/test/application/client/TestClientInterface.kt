package kr.weit.roadyfoody.test.application.client

import org.springframework.stereotype.Component
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@Component
@HttpExchange
interface TestClientInterface {
    @GetExchange("/todos/1")
    fun getTodo(): TodoResponse
}
