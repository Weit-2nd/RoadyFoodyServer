package kr.weit.roadyfoody.common.dto

class SliceResponse<T>(
    val contents: List<T>,
    val hasNext: Boolean,
) {
    constructor(size: Int, contents: List<T>) : this(
        if (contents.size > size)(contents.dropLast(1)) else contents,
        contents.size > size,
    )
}
