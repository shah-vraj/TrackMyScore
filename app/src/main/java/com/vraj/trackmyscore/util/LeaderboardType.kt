package com.vraj.trackmyscore.util

enum class LeaderboardType(val title: String) {
    MOST_VALUABLE_PLAYER("Most valuable player"),
    MOST_RUNS("Most runs"),
    AVERAGE("Average"),
    HIGHEST_INDIVIDUAL("Highest individual score"),
    MOST_WICKETS("Most wickets"),
    MOST_CATCHES("Most catches");

    companion object {
        fun getLeaderboardTypeList(): List<LeaderboardType> =
            listOf(
                MOST_RUNS,
                MOST_VALUABLE_PLAYER,
                AVERAGE,
                HIGHEST_INDIVIDUAL,
                MOST_WICKETS,
                MOST_CATCHES
            )
    }
}