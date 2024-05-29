package kr.weit.roadyfoody.swagger.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag

// TODO TestController를 지우게 되면 이 파일도 지워주세요
// TestController의 API 명세를 정의하는 interface이며
// 컨트롤러 코드가 더러워지는것을 방지하기 위해 저는 이런식으로 스펙을 정의해서 스펙이 모든 Swagger 관련 정보를 가지도록 합니다.
@Tag(name = SwaggerTag.TEST)
interface TestControllerSpec {
    @Operation(
        description = "성공 테스트 API",
        parameters = [
            Parameter(name = "name", description = "인사받을 이름", required = true, example = "김한빈"),
        ],
    )
    fun success(name: String): String

    @Operation(description = "에러 테스트 API")
    fun error(): String
}
