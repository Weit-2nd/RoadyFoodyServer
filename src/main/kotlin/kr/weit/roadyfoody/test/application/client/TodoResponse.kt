package kr.weit.roadyfoody.test.application.client

data class TodoResponse(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean,
)
