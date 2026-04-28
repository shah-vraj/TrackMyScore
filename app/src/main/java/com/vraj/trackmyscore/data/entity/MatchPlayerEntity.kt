package com.vraj.trackmyscore.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MatchPlayer")
data class MatchPlayerEntity(
    @PrimaryKey
    @ColumnInfo("playerId")
    val playerId: Int,

    @ColumnInfo("roundRuns")
    val roundRuns: Long = 0,

    @ColumnInfo("totalMatchRuns")
    val totalMatchRuns: Long = 0,

    @ColumnInfo("totalMatchWickets")
    val totalMatchWickets: Long = 0,

    @ColumnInfo("totalMatchCatches")
    val totalMatchCatches: Long = 0,

    @ColumnInfo("totalMatchOuts")
    val totalMatchOuts: Long = 0,

    @ColumnInfo("lastUpdatedDate")
    val lastUpdatedDate: String = "",

    @ColumnInfo("battingOrder")
    val battingOrder: Int = 0,

    @ColumnInfo("hasBattedInRound")
    val hasBattedInRound: Boolean = false
)