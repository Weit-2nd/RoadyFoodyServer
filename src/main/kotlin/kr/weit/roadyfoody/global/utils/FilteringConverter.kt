package kr.weit.roadyfoody.global.utils

object FilteringConverter {
    private const val DELIMITER = ","

    fun convertToCategoryIds(value: String): List<Long> {
        return splitValue(value)
            .map { it.toLong() }
    }

    private fun splitValue(value: String): List<String> {
        return value.split(DELIMITER)
    }
}
