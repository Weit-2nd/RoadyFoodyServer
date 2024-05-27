package kr.weit.roadyfoody.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// 요 컨틀롤러는 서버가 잘 떴는지 테스트용으로 만들어진 컨트롤러입니다.
// 나중에 여러분들이 작업을 시작하게되면 이 컨트롤러는 삭제해주세요
@RestController
@RequestMapping("/api/v1/test")
class TestController {
    @GetMapping("/success")
    fun success(): String {
        return "Hello World!"
    }

    @GetMapping("/error")
    fun error(): String {
        throw RuntimeException("test")
    }
}
