package kr.weit.roadyfoody.ranking.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Positive
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.global.swagger.ApiErrorCodeExamples
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.ranking.dto.UserRanking
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = SwaggerTag.RANKING)
interface RankingControllerSpec {
    @ApiErrorCodeExamples(
        [
            ErrorCode.SIZE_NON_POSITIVE,
        ],
    )
    @Operation(
        description = "리포트 랭킹 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리포트 랭킹 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema =
                            Schema(
                                implementation = UserRanking::class,
                            ),
                    ),
                ],
            ),

        ],
    )
    fun getReportRanking(
        @Positive(message = "size는 양수여야 합니다.")
        @RequestParam(defaultValue = "10")
        size: Long,
    ): List<UserRanking>

    @ApiErrorCodeExamples(
        [
            ErrorCode.SIZE_NON_POSITIVE,
        ],
    )
    @Operation(
        description = "리뷰 랭킹 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리뷰 랭킹 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema =
                            Schema(
                                implementation = UserRanking::class,
                            ),
                    ),
                ],
            ),
        ],
    )
    fun getReviewRanking(
        @Positive(message = "size는 양수여야 합니다.")
        @RequestParam(defaultValue = "10")
        size: Long,
    ): List<UserRanking>

    @ApiErrorCodeExamples(
        [
            ErrorCode.SIZE_NON_POSITIVE,
        ],
    )
    @Operation(
        description = "리뷰 좋아요 랭킹 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리뷰 좋아요 랭킹 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema =
                            Schema(
                                implementation = UserRanking::class,
                            ),
                    ),
                ],
            ),
        ],
    )
    fun getLikeRanking(
        @Positive(message = "size는 양수여야 합니다")
        @RequestParam(defaultValue = "10")
        size: Long,
    ): List<UserRanking>
}
