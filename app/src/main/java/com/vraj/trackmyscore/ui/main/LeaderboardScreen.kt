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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vraj.trackmyscore.R
import com.vraj.trackmyscore.data.entity.MatchPlayerEntity
import com.vraj.trackmyscore.data.entity.PlayerEntity
import com.vraj.trackmyscore.ui.base.BaseDropdown
import com.vraj.trackmyscore.ui.theme.BorderWhite
import com.vraj.trackmyscore.ui.theme.GlassWhite
import com.vraj.trackmyscore.util.LeaderboardType
import com.vraj.trackmyscore.viewmodel.MainViewModel

@Composable
fun LeaderboardScreen(
    navHostController: NavHostController,
    mainViewModel: MainViewModel
) {
    val players by mainViewModel.players.collectAsState()
    val matchPlayers by mainViewModel.matchPlayers.collectAsState()
    val selectedLeaderboardType by mainViewModel.selectedLeaderboardType.collectAsState()

    LaunchedEffect(true) {
        mainViewModel.updateData()
    }

    val sortedItems = remember(players, matchPlayers, selectedLeaderboardType) {
        val playerList = players.map { it.player }
        val sortedList = when (selectedLeaderboardType) {
            LeaderboardType.MOST_VALUABLE_PLAYER -> playerList.sortedByDescending { it.mvpScore }
            LeaderboardType.MOST_RUNS -> playerList.sortedByDescending { it.runs }
            LeaderboardType.AVERAGE -> playerList.sortedByDescending { it.average }
            LeaderboardType.HIGHEST_INDIVIDUAL -> playerList.sortedByDescending { it.highestScore }
            LeaderboardType.MOST_WICKETS -> playerList.sortedByDescending { it.wickets }
            LeaderboardType.MOST_CATCHES -> playerList.sortedByDescending { it.catches }
        }
        sortedList.map { p ->
            p to matchPlayers.find { it.playerId == p.id }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
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
                text = "Leaderboard",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(32.dp))
        }

        BaseDropdown(
            selectedItemName = selectedLeaderboardType.title,
            items = LeaderboardType.getLeaderboardTypeList(),
            getItemName = { it.title },
            onItemSelected = { mainViewModel.setSelectedLeaderboardType(it) }
        )

        if (sortedItems.isNotEmpty()) {
            PodiumSection(items = sortedItems.take(3), type = selectedLeaderboardType)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(
                    items = sortedItems.drop(3),
                    key = { _, pair -> pair.first.id }
                ) { index, (player, matchPlayer) ->
                    HallOfFameItem(
                        player = player,
                        matchPlayer = matchPlayer,
                        rank = index + 4,
                        type = selectedLeaderboardType
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumSection(items: List<Pair<PlayerEntity, MatchPlayerEntity?>>, type: LeaderboardType) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        // 2nd Place
        if (items.size >= 2) {
            PodiumPosition(
                player = items[1].first,
                matchPlayer = items[1].second,
                rank = 2,
                height = 120.dp,
                type = type,
                color = Color(0xFFC0C0C0)
            )
        }

        // 1st Place
        if (items.size >= 1) {
            PodiumPosition(
                player = items[0].first,
                matchPlayer = items[0].second,
                rank = 1,
                height = 160.dp,
                type = type,
                color = Color(0xFFFFD700)
            )
        }

        // 3rd Place
        if (items.size >= 3) {
            PodiumPosition(
                player = items[2].first,
                matchPlayer = items[2].second,
                rank = 3,
                height = 100.dp,
                type = type,
                color = Color(0xFFCD7F32)
            )
        }
    }
}

@Composable
private fun PodiumPosition(
    player: PlayerEntity,
    matchPlayer: MatchPlayerEntity?,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    type: LeaderboardType,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(player.color.copy(alpha = 0.8f))
                .border(2.dp, color, CircleShape)
        ) {
            Text(
                text = player.initials,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(height)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(color.copy(alpha = 0.6f), color.copy(alpha = 0.1f))
                    )
                )
                .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = color
                )
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.weight(1f))
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val matchValue = getMatchStatValue(matchPlayer, type)
                    if (matchValue != null && matchValue != "0") {
                        Text(
                            text = "↑$matchValue",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF4CAF50)
                        )
                    }
                    Text(
                        text = getStatValue(player, type),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun HallOfFameItem(player: PlayerEntity, matchPlayer: MatchPlayerEntity?, rank: Int, type: LeaderboardType) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite)
            .border(1.dp, BorderWhite.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.width(30.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(player.color.copy(alpha = 0.6f))
        ) {
            Text(
                text = player.initials,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = player.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.End) {
            val matchValue = getMatchStatValue(matchPlayer, type)
            if (matchValue != null && matchValue != "0") {
                Text(
                    text = "↑$matchValue",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF4CAF50)
                )
            }
            Text(
                text = getStatValue(player, type),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
    }
}

private fun getMatchStatValue(matchPlayer: MatchPlayerEntity?, type: LeaderboardType): String? {
    if (matchPlayer == null) return null
    return when (type) {
        LeaderboardType.MOST_RUNS -> matchPlayer.totalMatchRuns.toString()
        LeaderboardType.MOST_WICKETS -> matchPlayer.totalMatchWickets.toString()
        LeaderboardType.MOST_CATCHES -> matchPlayer.totalMatchCatches.toString()
        LeaderboardType.AVERAGE -> {
             // For average, we show the bump in runs as the primary driver
             if (matchPlayer.totalMatchRuns > 0) matchPlayer.totalMatchRuns.toString() else null
        }
        else -> null
    }
}

private fun getStatValue(player: PlayerEntity, type: LeaderboardType): String {
    return when (type) {
        LeaderboardType.MOST_VALUABLE_PLAYER -> player.mvpScoreString
        LeaderboardType.MOST_RUNS -> player.runs.toString()
        LeaderboardType.AVERAGE -> player.averageString
        LeaderboardType.HIGHEST_INDIVIDUAL -> player.highestScore.toString()
        LeaderboardType.MOST_WICKETS -> player.wickets.toString()
        LeaderboardType.MOST_CATCHES -> player.catches.toString()
    }
}