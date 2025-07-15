package com.vraj.trackmyscore.util

enum class BatterAction(val text: String, val runs: Long) {
    ZERO("0", 0),
    ONE("1", 1),
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    WK_BOWLED("WK-Bowled", 0),
    WK_CATCH("WK-Catch", 0),
    RETIRED_HURT("Retired-Hurt", 0),
    DIRECT_RUNS("Direct-Runs", 0);

    companion object {
        val actionList: List<BatterAction> =
            listOf(
                ZERO,
                ONE,
                TWO,
                THREE,
                FOUR,
                FIVE,
                SIX,
                WK_BOWLED,
                WK_CATCH,
                RETIRED_HURT,
                DIRECT_RUNS
            )
    }
}