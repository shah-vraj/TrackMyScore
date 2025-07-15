package com.vraj.trackmyscore.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vraj.trackmyscore.data.entity.PlayerEntity

@Dao
interface PlayerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlayer(playerEntity: PlayerEntity)

    @Query("DELETE FROM Player WHERE ID = :id")
    suspend fun removePlayer(id: Int)

    @Query("SELECT * FROM Player WHERE LOWER(Player.name) = LOWER(:name) LIMIT 1")
    suspend fun getPlayerWithName(name: String): PlayerEntity?

    @Query("SELECT * FROM Player")
    suspend fun getAllPlayers(): List<PlayerEntity>

    @Query("UPDATE Player SET runs = runs + :runs WHERE id = :id")
    suspend fun addRuns(id: Int, runs: Long)

    @Query("UPDATE Player SET wickets = wickets + 1 WHERE id = :id")
    suspend fun addWicket(id: Int)

    @Query("UPDATE Player SET catches = catches + 1 WHERE id = :id")
    suspend fun addCatch(id: Int)

    @Query("UPDATE Player SET outs = outs + 1 WHERE id = :id")
    suspend fun addOut(id: Int)

    @Query("UPDATE Player SET highest_score = :highestScore WHERE id = :id")
    suspend fun setHighestScore(id: Int, highestScore: Long)
}