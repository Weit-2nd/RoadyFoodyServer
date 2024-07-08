package kr.weit.roadyfoody.foodSpots.domain

enum class DayOfWeek(val num: Int) {
    MON(0),
    TUE(1),
    WED(2),
    THU(3),
    FRI(4),
    SAT(5),
    SUN(6),
    ;

    companion object {
        fun of(num: Int): DayOfWeek = entries.first { it.num == num }
    }
}
