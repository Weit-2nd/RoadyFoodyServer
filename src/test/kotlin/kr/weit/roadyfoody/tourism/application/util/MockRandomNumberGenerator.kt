package kr.weit.roadyfoody.tourism.application.util

class MockRandomNumberGenerator(
    private val randomNumbers: Set<Int>,
) : NumberGenerator {

    override fun generate(
        totalSize: Int,
        count: Int,
    ): Set<Int> {
        return randomNumbers
    }
}
