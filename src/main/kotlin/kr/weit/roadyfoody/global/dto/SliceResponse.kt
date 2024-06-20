package kr.weit.roadyfoody.global.dto

open class SliceResponse<T>(
    open val hasNext: Boolean,
    open val content: List<T>,
) {
    constructor(size: Int, content: List<T>) : this(
        content.size > size,
        if (content.size > size) content.dropLast(1) else content,
    )
}
