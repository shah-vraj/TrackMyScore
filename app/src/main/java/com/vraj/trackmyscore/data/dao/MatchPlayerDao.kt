package com.vraj.trackmyscore.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vraj.trackmyscore.data.entity.MatchPlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchPlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchPlayers(players: List<MatchPlayerEntity>)

    @Query("SELECT * FROM MatchPlayer ORDER BY battingOrder ASC")
    fun getAllMatchPlayers(): Flow<List<MatchPlayerEntity>>

    @Query("SELECT * FROM MatchPlayer WHERE playerId = :playerId LIMIT 1")
    suspend fun getMatchPlayer(playerId: Int): MatchPlayerEntity?

    @Query("UPDATE MatchPlayer SET roundRuns = roundRuns + :runs, totalMatchRuns = totalMatchRuns + :runs, lastUpdatedDate = :date WHERE playerId = :playerId")
    suspend fun addRuns(playerId: Int, runs: Long, date: String)

    @Query("UPDATE MatchPlayer SET totalMatchWickets = totalMatchWickets + 1, lastUpdatedDate = :date WHERE playerId = :playerId")
    suspend fun addWicket(playerId: Int, date: String)

    @Query("UPDATE MatchPlayer SET totalMatchCatches = totalMatchCatches + 1, lastUpdatedDate = :date WHERE playerId = :playerId")
    suspend fun addCatch(playerId: Int, date: String)

    @Query("UPDATE MatchPlayer SET totalMatchOuts = totalMatchOuts + 1, lastUpdatedDate = :date WHERE playerId = :playerId")
    suspend fun addOut(playerId: Int, date: String)

    @Query("UPDATE MatchPlayer SET totalMatchRuns = :runs, totalMatchWickets = :wickets, totalMatchCatches = :catches, totalMatchOuts = :outs, lastUpdatedDate = :date WHERE playerId = :playerId")
    suspend fun resetAndAddStats(playerId: Int, runs: Long, wickets: Long, catches: Long, outs: Long, date: String)

    @Query("UPDATE MatchPlayer SET hasBattedInRound = 1 WHERE playerId = :playerId")
    suspend fun markAsBatted(playerId: Int)

    @Query("UPDATE MatchPlayer SET roundRuns = 0, hasBattedInRound = 0")
    suspend fun resetRoundData()

    @Update
    suspend fun updateMatchPlayers(players: List<MatchPlayerEntity>)

    @Query("DELETE FROM MatchPlayer")
    suspend fun clearMatch()
}