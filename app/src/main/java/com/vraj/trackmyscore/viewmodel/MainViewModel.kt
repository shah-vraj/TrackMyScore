package com.vraj.trackmyscore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vraj.trackmyscore.R
import com.vraj.trackmyscore.data.entity.MatchPlayerEntity
import com.vraj.trackmyscore.data.entity.PlayerEntity
import com.vraj.trackmyscore.data.repository.PlayerRepository
import com.vraj.trackmyscore.di.IoDispatcher
import com.vraj.trackmyscore.model.AlertDialogData
import com.vraj.trackmyscore.model.SelectablePlayer
import com.vraj.trackmyscore.util.AppSharedPreferences
import com.vraj.trackmyscore.util.AppSharedPreferences.Companion.INVALID_ID
import com.vraj.trackmyscore.util.BatterAction
import com.vraj.trackmyscore.util.BatterAction.DIRECT_RUNS
import com.vraj.trackmyscore.util.BatterAction.FIVE
import com.vraj.trackmyscore.util.BatterAction.FOUR
import com.vraj.trackmyscore.util.BatterAction.ONE
import com.vraj.trackmyscore.util.BatterAction.RETIRED_HURT
import com.vraj.trackmyscore.util.BatterAction.SIX
import com.vraj.trackmyscore.util.BatterAction.THREE
import com.vraj.trackmyscore.util.BatterAction.TWO
import com.vraj.trackmyscore.util.BatterAction.WK_BOWLED
import com.vraj.trackmyscore.util.BatterAction.WK_CATCH
import com.vraj.trackmyscore.util.BatterAction.ZERO
import com.vraj.trackmyscore.util.LeaderboardType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val appSharedPreferences: AppSharedPreferences,
) : ViewModel() {

    private val _players = MutableStateFlow<List<SelectablePlayer>>(listOf())
    val players = _players.asStateFlow()

    private val _errorMessage = MutableStateFlow(INVALID_ERROR_MESSAGE_ID)
    val errorMessage = _errorMessage.asStateFlow()

    private val _showAlertDialog = MutableStateFlow<AlertDialogData?>(null)
    val showAlertDialog = _showAlertDialog.asStateFlow()

    private val _toastMessage = MutableStateFlow<Int?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    private val _currentBatter = MutableStateFlow(PlayerEntity.dummy)
    val currentBatter = _currentBatter.asStateFlow()

    private val _currentBatterRuns = MutableStateFlow(0L)
    val currentBatterRuns = _currentBatterRuns.asStateFlow()

    private val _selectedBatsman = MutableStateFlow(PlayerEntity.dummy)
    val selectedBatsman = _selectedBatsman.asStateFlow()

    private val _selectedBowler = MutableStateFlow(PlayerEntity.dummy)
    val selectedBowler = _selectedBowler.asStateFlow()

    private val _selectedCatcher = MutableStateFlow(PlayerEntity.dummy)
    val selectedCatcher = _selectedCatcher.asStateFlow()

    private val _selectedDirectRuns = MutableStateFlow("")
    val selectedDirectRuns = _selectedDirectRuns.asStateFlow()

    private val _batterActions = MutableStateFlow(BatterAction.actionList.map { it to false })
    val batterActions = _batterActions.asStateFlow()

    private val _selectedLeaderboardType = MutableStateFlow(LeaderboardType.MOST_RUNS)
    val selectedLeaderboardType = _selectedLeaderboardType.asStateFlow()

    val matchPlayers = playerRepository.getAllMatchPlayers().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val battingOrderList = combine(matchPlayers, players) { mPlayers, pList ->
        mPlayers.mapNotNull { mp ->
            pList.find { it.player.id == mp.playerId }?.let { sp ->
                Triple(mp, sp.player, mp.hasBattedInRound)
            }
        }.sortedBy { it.first.battingOrder }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _roundSummary = MutableStateFlow<List<Pair<PlayerEntity, Long>>?>(null)
    val roundSummary = _roundSummary.asStateFlow()

    val inGamePlayers = players.map {
        val players = it.filter { gamePlayer -> gamePlayer.isSelected }
            .map { gamePlayer -> gamePlayer.player }
        buildList {
            add(PlayerEntity.dummy)
            addAll(players)
        }
    }

    init {
        updateData()
    }

    private suspend fun refreshAutoSelection() {
        val mPlayers = playerRepository.getAllMatchPlayers().first()
        val allPlayers = playerRepository.getAllPlayers()

        if (_currentBatter.value.id == INVALID_ID && mPlayers.isNotEmpty()) {
            val order = mPlayers.mapNotNull { mp ->
                allPlayers.find { it.id == mp.playerId }?.let { p ->
                    Triple(mp, p, mp.hasBattedInRound)
                }
            }.sortedBy { it.first.battingOrder }

            if (order.isNotEmpty()) {
                val upcoming = order.filter { !it.third }
                val nextBatterTriple = upcoming.firstOrNull() ?: order[0]

                _selectedBatsman.value = nextBatterTriple.second

                val idx = order.indexOf(nextBatterTriple)
                val bowlerIdx = (idx + 1) % order.size
                _selectedBowler.value = order[bowlerIdx].second
            }
        }
    }

    fun updateData() {
        viewModelScope.launch(ioDispatcher) {
            updatePlayers()
        }
    }

    fun addPlayerIfExists(name: String, completion: () -> Unit) {
        if(name.isBlank()) {
            _errorMessage.value = R.string.txt_name_cannot_be_empty
            return
        }

        viewModelScope.launch(ioDispatcher) {
            playerRepository.getPlayerWithName(name)?.let {
                _errorMessage.value = R.string.txt_player_already_added
            } ?: run {
                _errorMessage.value = INVALID_ERROR_MESSAGE_ID
                addPlayer(name)
                withContext(Dispatchers.Main) {
                    completion()
                }
            }
        }
    }

    fun removePlayer(id: Int) {
        viewModelScope.launch(ioDispatcher) {
            playerRepository.removePlayer(id)
            _players.value = _players.value.filter { it.player.id != id }
        }
    }

    fun showAlertDialog(alertDialogData: AlertDialogData?) {
        _showAlertDialog.value = alertDialogData
    }

    fun setSelection(id: Int, isSelected: Boolean) {
        _players.value = _players.value.map {
            if(it.player.id != id)
                return@map it
            return@map it.copy(isSelected = isSelected)
        }
    }

    fun fetchCurrentBatter() {
        with(appSharedPreferences) {
            val currentId = getCurrentBatterId()
            _players.value.find {
                it.player.id == currentId
            }?.let {
                _currentBatter.value = it.player
                _selectedBatsman.value = it.player
                _currentBatterRuns.value = getCurrentBatterRuns()
            } ?: run {
                _currentBatter.value = PlayerEntity.dummy
                _currentBatterRuns.value = getCurrentBatterRuns()
                // Don't reset _selectedBatsman here, let the batting order observer handle it
            }
        }
    }

    fun setSelectedBatsman(playerEntity: PlayerEntity) {
        _selectedBatsman.value = playerEntity
    }

    fun setSelectedBowler(playerEntity: PlayerEntity) {
        _selectedBowler.value = playerEntity
    }

    fun setSelectedCatcher(playerEntity: PlayerEntity) {
        _selectedCatcher.value = playerEntity
    }

    fun setSelectedDirectRuns(runs: String) {
        _selectedDirectRuns.value = runs
    }

    fun setSelectedLeaderboardType(type: LeaderboardType) {
        _selectedLeaderboardType.value = type
    }

    fun shuffleMatchOrder() {
        viewModelScope.launch(ioDispatcher) {
            val current = matchPlayers.value
            if (current.isEmpty()) return@launch
            val shuffled = current.shuffled().mapIndexed { index, mp ->
                mp.copy(battingOrder = index)
            }
            playerRepository.updateMatchPlayers(shuffled)
            refreshAutoSelection()
        }
    }

    fun moveMatchPlayer(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch(ioDispatcher) {
            val list = matchPlayers.value.toMutableList()
            if (fromIndex !in list.indices || toIndex !in list.indices) return@launch
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            val updated = list.mapIndexed { index, mp ->
                mp.copy(battingOrder = index)
            }
            playerRepository.updateMatchPlayers(updated)
            refreshAutoSelection()
        }
    }

    fun startNewMatch(completion: () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            playerRepository.clearMatch()
            updatePlayers()
            val selected = _players.value.filter { it.isSelected }.map { it.player }
            if (selected.size < 2) return@launch

            val initialMatchPlayers = selected.shuffled().mapIndexed { index, player ->
                MatchPlayerEntity(
                    playerId = player.id,
                    battingOrder = index
                )
            }
            playerRepository.insertMatchPlayers(initialMatchPlayers)
            refreshAutoSelection()

            withContext(Dispatchers.Main) {
                completion()
            }
        }
    }

    fun startInnings() {
        viewModelScope.launch(ioDispatcher) {
            val selectedPlayers = _players.value.filter { it.isSelected }.map { it.player }
            if (selectedPlayers.size < 2) return@launch

            // If it's a new match, initialize MatchPlayers with shuffle
            val existingMatch = playerRepository.getAllMatchPlayers().first()
            if (existingMatch.isEmpty()) {
                val initialMatchPlayers = selectedPlayers.shuffled().mapIndexed { index, player ->
                    MatchPlayerEntity(
                        playerId = player.id,
                        battingOrder = index
                    )
                }
                playerRepository.insertMatchPlayers(initialMatchPlayers)
            }

            with(appSharedPreferences) {
                _players.value.find {
                    _selectedBatsman.value.id == it.player.id
                }?.let {
                    _currentBatter.value = it.player
                    setCurrentBatterId(it.player.id)
                    setCurrentBatterRuns(0L)
                    fetchCurrentBatter()
                }
            }
        }
    }

    fun selectBatterAction(action: BatterAction) {
        _batterActions.value = _batterActions.value.map {
            it.first to (it.first == action)
        }
    }

    fun recordInnings() {
        viewModelScope.launch(ioDispatcher) {
            val action = _batterActions.value.find { it.second }?.first ?: run {
                _toastMessage.value = R.string.err_select_action
                return@launch
            }

            when (action) {
                ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, DIRECT_RUNS -> {
                    val runs = if (action == DIRECT_RUNS)
                        _selectedDirectRuns.value.toLongOrNull() ?: 0
                    else action.runs
                    playerRepository.addRuns(_currentBatter.value.id, runs)
                    playerRepository.addMatchRuns(_currentBatter.value.id, runs)
                    appSharedPreferences.apply {
                        setCurrentBatterRuns(getCurrentBatterRuns() + runs)
                    }
                }
                WK_BOWLED, WK_CATCH -> {
                    if (_selectedBowler.value.id == INVALID_ID) {
                        _toastMessage.value = R.string.err_select_bowler
                        return@launch
                    }

                    if (action == WK_CATCH && _selectedCatcher.value.id == INVALID_ID) {
                        _toastMessage.value = R.string.err_select_catcher
                        return@launch
                    }

                    playerRepository.addOut(_selectedBatsman.value.id)
                    playerRepository.addMatchOut(_selectedBatsman.value.id)
                    
                    playerRepository.addWicket(_selectedBowler.value.id)
                    playerRepository.addMatchWicket(_selectedBowler.value.id)

                    if (action == WK_CATCH) {
                        playerRepository.addCatch(_selectedCatcher.value.id)
                        playerRepository.addMatchCatch(_selectedCatcher.value.id)
                    }

                    endInnings()
                }
                RETIRED_HURT -> endInnings()
            }

            fetchCurrentBatter()
            _batterActions.value = _batterActions.value.map { it.first to false }
            _toastMessage.value = R.string.txt_action_recorded
            _selectedCatcher.value = PlayerEntity.dummy
            _selectedDirectRuns.value = ""
        }
    }

    fun onToastShown() {
        _toastMessage.value = null
    }

    private suspend fun addPlayer(name: String) {
        playerRepository.addPlayer(PlayerEntity(name = name))
        _players.value = buildList {
            addAll(_players.value)
            playerRepository.getPlayerWithName(name)?.let {
                add(SelectablePlayer(it, true))
            }
        }
    }

    private suspend fun updatePlayers() {
        val players = playerRepository.getAllPlayers()
        _players.value = _players.value.let { currentPlayers ->
            if (currentPlayers.isEmpty())
                return@let players.map { SelectablePlayer(it, true) }
            players.map {
                val isSelected = currentPlayers.find { p ->
                    p.player.id == it.id
                }?.isSelected ?: true
                SelectablePlayer(it, isSelected)
            }
        }
    }

    private suspend fun endInnings() {
        with(appSharedPreferences) {
            updatePlayers()
            val allPlayers = playerRepository.getAllPlayers()

            _players.value.find { it.player.id == _currentBatter.value.id }?.let {
                val currentRuns = getCurrentBatterRuns()
                if (currentRuns > it.player.highestScore)
                    playerRepository.setHighestScore(it.player.id, currentRuns)
            }

            playerRepository.markAsBatted(_currentBatter.value.id)

            // Check if round is over
            val currentMatchPlayers = playerRepository.getAllMatchPlayers().first()
            if (currentMatchPlayers.all { it.hasBattedInRound }) {
                // Prepare summary data before resetting
                
                // Sort by runs (desc) then by current batting order (asc) for tie-breaking
                val sortedCurrentMatchPlayers = currentMatchPlayers.sortedWith(
                    compareByDescending<MatchPlayerEntity> { it.roundRuns }
                        .thenBy { it.battingOrder }
                )

                val summaryData = sortedCurrentMatchPlayers.mapNotNull { mp ->
                    allPlayers.find { it.id == mp.playerId }?.let { 
                        it to mp.roundRuns 
                    }
                }
                
                _roundSummary.value = summaryData

                // Round Over! Sort by roundRuns and start new round
                val nextOrderPlayers = sortedCurrentMatchPlayers.mapIndexed { index, player ->
                    player.copy(
                        battingOrder = index,
                        roundRuns = 0,
                        hasBattedInRound = false
                    )
                }
                playerRepository.updateMatchPlayers(nextOrderPlayers)
                _toastMessage.value = R.string.txt_round_completed_new_order
            }

            setCurrentBatterId(INVALID_ID)
            setCurrentBatterRuns(0L)
            fetchCurrentBatter()
            refreshAutoSelection()
        }
    }

    fun dismissRoundSummary() {
        _roundSummary.value = null
    }

    companion object {
        const val INVALID_ERROR_MESSAGE_ID = -1
    }
}