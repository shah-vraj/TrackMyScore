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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vraj.trackmyscore.R
import com.vraj.trackmyscore.data.entity.PlayerEntity
import com.vraj.trackmyscore.model.SelectablePlayer
import com.vraj.trackmyscore.ui.base.BaseDropdown
import com.vraj.trackmyscore.ui.theme.BorderWhite
import com.vraj.trackmyscore.ui.theme.GlassWhite
import com.vraj.trackmyscore.util.LeaderboardType
import com.vraj.trackmyscore.viewmodel.MainViewModel

@Composable
fun StatsScreen(
    navHostController: NavHostController,
    viewModel: MainViewModel
) {
    val players by viewModel.players.collectAsState()
    var selectedPlayer by remember { mutableStateOf(PlayerEntity.dummy) }
    val scrollState = rememberScrollState()

    LaunchedEffect(true) {
        viewModel.updateData()
    }

    LaunchedEffect(players) {
        if (selectedPlayer.id == -1) {
            selectedPlayer = players.firstOrNull()?.player ?: PlayerEntity.dummy
        } else {
            selectedPlayer = players.find { it.player.id == selectedPlayer.id }?.player ?: selectedPlayer
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(GlassWhite)
                    .clickable { navHostController.popBackStack() }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Player Stats",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(32.dp))
        }

        BaseDropdown(
            selectedItemName = selectedPlayer.name,
            items = players.map { it.player },
            getItemName = { it.name },
            onItemSelected = { selectedPlayer = it }
        )

        if (selectedPlayer.id != -1) {
            PlayerBioCard(player = selectedPlayer)

            // Replaced LazyVerticalGrid with a manual column of rows to avoid 
            // conflicting scroll behaviors and choppiness within the verticalScroll.
            StatsGrid(
                selectedPlayer = selectedPlayer,
                players = players
            )
        }
    }
}

@Composable
private fun StatsGrid(
    selectedPlayer: PlayerEntity,
    players: List<SelectablePlayer>
) {
    val stats = listOf(
        Triple("Runs", selectedPlayer.runs.toString(), LeaderboardType.MOST_RUNS),
        Triple("Wickets", selectedPlayer.wickets.toString(), LeaderboardType.MOST_WICKETS),
        Triple("Average", selectedPlayer.averageString, LeaderboardType.AVERAGE),
        Triple("High Score", selectedPlayer.highestScore.toString(), LeaderboardType.HIGHEST_INDIVIDUAL),
        Triple("Catches", selectedPlayer.catches.toString(), LeaderboardType.MOST_CATCHES),
        Triple("MVP Points", selectedPlayer.mvpScoreString, LeaderboardType.MOST_VALUABLE_PLAYER)
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        stats.chunked(2).forEach { rowStats ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowStats.forEach { (label, value, type) ->
                    StatCard(
                        value = value,
                        category = label,
                        rank = getRankForPlayer(selectedPlayer.id, players, type),
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowStats.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PlayerBioCard(player: PlayerEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassWhite)
            .border(1.dp, BorderWhite, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(player.color.copy(alpha = 0.8f))
                    .border(3.dp, Color.White, CircleShape)
            ) {
                Text(
                    text = player.initials,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Column {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "MVP SCORE",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = player.mvpScoreString,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFFD700) // Gold
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    value: String,
    category: String,
    rank: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GlassWhite)
            .border(1.dp, BorderWhite, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                ),
                color = Color.White
            )
            Text(
                text = category.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = Color.White.copy(alpha = 0.5f)
            )
        }

        // Rank Badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 8.dp, y = (-8).dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when (rank) {
                        1 -> Color(0xFFFFD700) // Gold
                        2 -> Color(0xFFC0C0C0) // Silver
                        3 -> Color(0xFFCD7F32) // Bronze
                        else -> Color.White.copy(alpha = 0.2f)
                    }
                )
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (rank in 1..3) Color.Black else Color.White
            )
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