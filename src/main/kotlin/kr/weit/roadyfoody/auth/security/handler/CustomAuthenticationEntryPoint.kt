package kr.weit.roadyfoody.auth.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.ErrorResponse
<<<<<<< HEAD:src/main/kotlin/kr/weit/roadyfoody/user/security/handler/CustomAuthenticationEntryPoint.kt
=======
import kr.weit.roadyfoody.global.utils.ObjectMapperProvider
>>>>>>> 9845b5c0a8559be075801c18b0eeb7283f1d8779:src/main/kotlin/kr/weit/roadyfoody/auth/security/handler/CustomAuthenticationEntryPoint.kt
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        response.contentType = "application/json"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        objectMapper.writeValue(
            response?.outputStream,
            ErrorResponse.of(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.errorMessage),
        )
    }
}
