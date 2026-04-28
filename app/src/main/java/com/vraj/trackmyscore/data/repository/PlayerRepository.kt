package com.vraj.trackmyscore.data.repository

import com.vraj.trackmyscore.data.dao.MatchPlayerDao
import com.vraj.trackmyscore.data.dao.PlayerDao
import com.vraj.trackmyscore.data.entity.MatchPlayerEntity
import com.vraj.trackmyscore.data.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PlayerRepository {
    suspend fun addPlayer(playerEntity: PlayerEntity)

    suspend fun removePlayer(id: Int)

    suspend fun getPlayerWithName(name: String): PlayerEntity?

    suspend fun getAllPlayers(): List<PlayerEntity>

    suspend fun addRuns(id: Int, runs: Long)

    suspend fun addWicket(id: Int)

    suspend fun addCatch(id: Int)

    suspend fun addOut(id: Int)

    suspend fun setHighestScore(id: Int, highestScore: Long)

    // Match Player methods
    suspend fun insertMatchPlayers(players: List<MatchPlayerEntity>)
    fun getAllMatchPlayers(): Flow<List<MatchPlayerEntity>>
    suspend fun getMatchPlayer(playerId: Int): MatchPlayerEntity?
    suspend fun addMatchRuns(playerId: Int, runs: Long)
    suspend fun addMatchWicket(playerId: Int)
    suspend fun addMatchCatch(playerId: Int)
    suspend fun addMatchOut(playerId: Int)
    suspend fun markAsBatted(playerId: Int)
    suspend fun resetRoundData()
    suspend fun updateMatchPlayers(players: List<MatchPlayerEntity>)
    suspend fun clearMatch()
}

class PlayerRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao,
    private val matchPlayerDao: MatchPlayerDao
) : PlayerRepository {
    override suspend fun addPlayer(playerEntity: PlayerEntity) =
        playerDao.addPlayer(playerEntity)

    override suspend fun removePlayer(id: Int) =
        playerDao.removePlayer(id)

    override suspend fun getPlayerWithName(name: String): PlayerEntity? =
        playerDao.getPlayerWithName(name)

    override suspend fun getAllPlayers(): List<PlayerEntity> =
        playerDao.getAllPlayers()

    override suspend fun addRuns(id: Int, runs: Long) =
        playerDao.addRuns(id, runs)

    override suspend fun addWicket(id: Int) =
        playerDao.addWicket(id)

    override suspend fun addCatch(id: Int) =
        playerDao.addCatch(id)

    override suspend fun addOut(id: Int) =
        playerDao.addOut(id)

    override suspend fun setHighestScore(id: Int, highestScore: Long) =
        playerDao.setHighestScore(id, highestScore)

    override suspend fun insertMatchPlayers(players: List<MatchPlayerEntity>) =
        matchPlayerDao.insertMatchPlayers(players)

    override fun getAllMatchPlayers(): Flow<List<MatchPlayerEntity>> =
        matchPlayerDao.getAllMatchPlayers()

    override suspend fun getMatchPlayer(playerId: Int): MatchPlayerEntity? =
        matchPlayerDao.getMatchPlayer(playerId)

    override suspend fun addMatchRuns(playerId: Int, runs: Long) {
        val today = getTodayDate()
        val matchPlayer = matchPlayerDao.getMatchPlayer(playerId)
        if (matchPlayer != null && matchPlayer.lastUpdatedDate != today && matchPlayer.lastUpdatedDate.isNotEmpty()) {
            matchPlayerDao.resetAndAddStats(playerId, runs, 0, 0, 0, today)
        } else {
            matchPlayerDao.addRuns(playerId, runs, today)
        }
    }

    override suspend fun addMatchWicket(playerId: Int) {
        val today = getTodayDate()
        val matchPlayer = matchPlayerDao.getMatchPlayer(playerId)
        if (matchPlayer != null && matchPlayer.lastUpdatedDate != today && matchPlayer.lastUpdatedDate.isNotEmpty()) {
            matchPlayerDao.resetAndAddStats(playerId, 0, 1, 0, 0, today)
        } else {
            matchPlayerDao.addWicket(playerId, today)
        }
    }

    override suspend fun addMatchCatch(playerId: Int) {
        val today = getTodayDate()
        val matchPlayer = matchPlayerDao.getMatchPlayer(playerId)
        if (matchPlayer != null && matchPlayer.lastUpdatedDate != today && matchPlayer.lastUpdatedDate.isNotEmpty()) {
            matchPlayerDao.resetAndAddStats(playerId, 0, 0, 1, 0, today)
        } else {
            matchPlayerDao.addCatch(playerId, today)
        }
    }

    override suspend fun addMatchOut(playerId: Int) {
        val today = getTodayDate()
        val matchPlayer = matchPlayerDao.getMatchPlayer(playerId)
        if (matchPlayer != null && matchPlayer.lastUpdatedDate != today && matchPlayer.lastUpdatedDate.isNotEmpty()) {
            matchPlayerDao.resetAndAddStats(playerId, 0, 0, 0, 1, today)
        } else {
            matchPlayerDao.addOut(playerId, today)
        }
    }

    private fun getTodayDate(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    }

    override suspend fun markAsBatted(playerId: Int) =
        matchPlayerDao.markAsBatted(playerId)

    override suspend fun resetRoundData() =
        matchPlayerDao.resetRoundData()

    override suspend fun updateMatchPlayers(players: List<MatchPlayerEntity>) =
        matchPlayerDao.updateMatchPlayers(players)

    override suspend fun clearMatch() =
        matchPlayerDao.clearMatch()
}