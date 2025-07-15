package com.vraj.trackmyscore.data.repository

import com.vraj.trackmyscore.data.dao.PlayerDao
import com.vraj.trackmyscore.data.entity.PlayerEntity
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
}

class PlayerRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao
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
}