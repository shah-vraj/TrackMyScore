package com.vraj.trackmyscore.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.vraj.trackmyscore.R
import com.vraj.trackmyscore.data.entity.PlayerEntity
import com.vraj.trackmyscore.model.SelectablePlayer
import com.vraj.trackmyscore.util.LeaderboardType
import com.vraj.trackmyscore.viewmodel.MainViewModel

@Composable
fun StatsScreen(
    navHostController: NavHostController,
    viewModel: MainViewModel
) {
    val players by viewModel.players.collectAsState()
    var selectedPlayer by remember { mutableStateOf(PlayerEntity.dummy) }

    LaunchedEffect(true) {
        viewModel.updateData()
    }

    LaunchedEffect(players) {
        selectedPlayer = players.firstOrNull()?.player ?: PlayerEntity.dummy
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(25.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back",
                modifier = Modifier
                    .clickable { navHostController.popBackStack() }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(id = R.string.txt_stats),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        PlayerDropdownItem(
            selectedPlayer = selectedPlayer,
            players = players.map { it.player },
            onItemSelected = { selectedPlayer = it }
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatItem(
                    value = selectedPlayer.runs.toString(),
                    category = "Runs",
                    rank = getRankForPlayer(selectedPlayer.id, players, LeaderboardType.MOST_RUNS)
                )
                StatItem(
                    value = selectedPlayer.wickets.toString(),
                    category = "Wickets",
                    rank = getRankForPlayer(selectedPlayer.id, players, LeaderboardType.MOST_WICKETS)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatItem(
                    value = selectedPlayer.averageString,
                    category = "Average",
                    rank = getRankForPlayer(selectedPlayer.id, players, LeaderboardType.AVERAGE)
                )
                StatItem(
                    value = selectedPlayer.highestScore.toString(),
                    category = "Highest score",
                    rank = getRankForPlayer(selectedPlayer.id, players, LeaderboardType.HIGHEST_INDIVIDUAL)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatItem(
                    value = selectedPlayer.catches.toString(),
                    category = "Catches",
                    rank = getRankForPlayer(selectedPlayer.id, players, LeaderboardType.MOST_CATCHES)
                )
                StatItem(
                    value = selectedPlayer.mvpScoreString,
                    category = "MVP points",
                    rank = getRankForPlayer(selectedPlayer.id, players, LeaderboardType.MOST_VALUABLE_PLAYER)
                )
            }
        }
    }
}

@Composable
fun StatItem(
    value: String,
    category: String,
    rank: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy((-10).dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy((-12).dp),
            modifier = modifier
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(140.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                    .zIndex(1f)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(140.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(top = 20.dp, bottom = 12.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(20.dp)
                .offset(y = (-10).dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun PlayerDropdownItem(
    selectedPlayer: PlayerEntity,
    players: List<PlayerEntity>,
    onItemSelected: (PlayerEntity) -> Unit
) {
    var isDropDownExpanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Box {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    isDropDownExpanded = true
                }
            ) {
                Text(
                    text = selectedPlayer.name,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_drop_down),
                    contentDescription = "DropDown Icon"
                )
            }

            DropdownMenu(
                expanded = isDropDownExpanded,
                onDismissRequest = {
                    isDropDownExpanded = false
                }) {
                players.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(text = type.name) },
                        onClick = {
                            isDropDownExpanded = false
                            onItemSelected(type)
                        }
                    )
                }
            }
        }
    }
}

private fun getRankForPlayer(
    playerId: Int,
    selectablePlayers: List<SelectablePlayer>,
    type: LeaderboardType
) = selectablePlayers
    .map { it.player }
    .let { players ->
        when (type) {
            LeaderboardType.MOST_VALUABLE_PLAYER -> players.sortedByDescending { it.mvpScore }
            LeaderboardType.MOST_RUNS -> players.sortedByDescending { it.runs }
            LeaderboardType.AVERAGE -> players.sortedByDescending { it.average }
            LeaderboardType.HIGHEST_INDIVIDUAL -> players.sortedByDescending { it.highestScore }
            LeaderboardType.MOST_WICKETS -> players.sortedByDescending { it.wickets }
            LeaderboardType.MOST_CATCHES -> players.sortedByDescending { it.catches }
        }
    }
    .indexOfFirst { it.id == playerId } + 1