package kr.weit.roadyfoody.tourism.application.util

class RandomNumberGenerator : NumberGenerator {
    override fun generate(
        totalSize: Int,
        count: Int,
    ): Set<Int> {
        val randomNumbers = mutableSetOf<Int>()
        while (randomNumbers.size < count) {
            randomNumbers.add((0 until totalSize).random())
        }
        return randomNumbers
    }
}
