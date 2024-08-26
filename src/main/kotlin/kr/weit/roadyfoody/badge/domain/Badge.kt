package kr.weit.roadyfoody.badge.domain

enum class Badge(
    val description: String,
) {
    BEGINNER("초심자"),
    PRO("중수"),
    SUPER("고수"),
    EXPERT("초고수"),
}
