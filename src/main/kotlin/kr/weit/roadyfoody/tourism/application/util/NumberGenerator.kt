package kr.weit.roadyfoody.tourism.application.util

import org.springframework.stereotype.Component

@Component
interface NumberGenerator {
    fun generate(
        totalSize: Int,
        count: Int,
    ): Set<Int>
}
