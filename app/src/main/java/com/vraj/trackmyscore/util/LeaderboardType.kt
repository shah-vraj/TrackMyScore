package com.vraj.trackmyscore.util

enum class LeaderboardType(val title: String) {
    MOST_RUNS("Most runs"),
    AVERAGE("Average"),
    HIGHEST_INDIVIDUAL("Highest individual score"),
    MOST_WICKETS("Most wickets"),
    MOST_CATCHES("Most catches");

    companion object {
        fun getLeaderboardTypeList(): List<LeaderboardType> =
            listOf(MOST_RUNS, AVERAGE, HIGHEST_INDIVIDUAL, MOST_WICKETS, MOST_CATCHES)
    }
}