//package com.vraj.trackmyscore.ui.main
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import com.vraj.kachufulscorecounter.R
//import com.vraj.trackmyscore.data.entity.PlayerEntity
//import com.vraj.trackmyscore.viewmodel.MainViewModel
//import java.math.RoundingMode
//import java.time.LocalDateTime
//
//@Composable
//fun StatsScreen(
//    navHostController: NavHostController,
//    mainViewModel: MainViewModel
//) {
//    val players by mainViewModel.players.collectAsState()
//    val games by mainViewModel.games.collectAsState()
//    LaunchedEffect(true) { mainViewModel.updateData() }
//
//    Column(
//        verticalArrangement = Arrangement.spacedBy(40.dp),
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(top = 15.dp)
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.ic_back),
//                contentDescription = "back",
//                modifier = Modifier
//                    .clickable { navHostController.popBackStack() }
//            )
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            Text(
//                text = stringResource(id = R.string.txt_stats),
//                style = MaterialTheme.typography.titleLarge,
//                color = Color.White
//            )
//
//            Spacer(modifier = Modifier.weight(1f))
//        }
//
//        LazyColumn {
//            players
//                .map { it.player }
//                .sortedByDescending { it.score }
//                .let { sortedPlayers -> items(sortedPlayers) { PlayerItem(it, games) } }
//        }
//    }
//}
//
//@Composable
//private fun PlayerItem(player: PlayerEntity, games: List<GameEntity>) {
//    val playerGames by remember { mutableStateOf(getGamesForPlayer(player.id, games)) }
//
//    Column(
//        verticalArrangement = Arrangement.spacedBy(15.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.padding(10.dp)
//    ) {
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(15.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier
//                    .size(50.dp)
//                    .clip(CircleShape)
//                    .background(player.color)
//            ) {
//                Text(
//                    text = player.initials,
//                    style = MaterialTheme.typography.labelMedium,
//                    color = Color.White
//                )
//            }
//
//            Text(
//                text = player.name,
//                style = MaterialTheme.typography.titleMedium,
//                color = Color.White
//            )
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            Text(
//                text = player.score.toString(),
//                style = MaterialTheme.typography.titleMedium,
//                color = Color.White
//            )
//        }
//
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(10.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                text = stringResource(R.string.txt_games),
//                style = MaterialTheme.typography.titleMedium,
//                color = Color.White
//            )
//
//            Text(
//                text = buildString {
//                    append(getNumberOfWins(player.id, playerGames))
//                    append("W / ")
//                    append(playerGames.size)
//                    append("T -- ")
//                    if (playerGames.isEmpty()) {
//                        append("0%")
//                        return@buildString
//                    }
//
//                    append(
//                        ((getNumberOfWins(
//                            player.id,
//                            playerGames
//                        ) / playerGames.size.toFloat()) * 100)
//                            .toBigDecimal().setScale(2, RoundingMode.UP)
//                    )
//                    append("%")
//                },
//                style = MaterialTheme.typography.titleSmall,
//                color = Color.LightGray
//            )
//        }
//
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(10.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                text = stringResource(R.string.txt_highest_score),
//                style = MaterialTheme.typography.titleMedium,
//                color = Color.White
//            )
//
//            Text(
//                text = getMaxSessionTotalForPlayer(player.id, playerGames).toString(),
//                style = MaterialTheme.typography.titleSmall,
//                color = Color.LightGray
//            )
//        }
//
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(10.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                text = stringResource(R.string.txt_average_score),
//                style = MaterialTheme.typography.titleMedium,
//                color = Color.White
//            )
//
//            Text(
//                text = getAverageSessionScoreForPlayer(player.id, playerGames).toString(),
//                style = MaterialTheme.typography.titleSmall,
//                color = Color.LightGray
//            )
//        }
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(1.dp)
//                .background(Color.Gray)
//        )
//    }
//}
//
//private fun getGamesForPlayer(playerId: Int, games: List<GameEntity>): List<GameEntity> =
//    games.filter {
//        it.scores.playerScores.any { playerScore ->
//            playerScore.player.id == playerId
//        }
//    }
//
//private fun getNumberOfWins(playerId: Int, games: List<GameEntity>): Int =
//    games.count { gameEntity ->
//        gameEntity.scores.playerScores
//            .find { it.player.id == playerId }
//            ?.let { it.score > 0 }
//            ?: false
//    }
//
//private fun getMaxSessionTotalForPlayer(playerId: Int, games: List<GameEntity>): Long =
//    games.groupBy { getSessionStartForDateTime(it.date) }
//        .values.maxOfOrNull { sessionGames ->
//            sessionGames.sumOf { game ->
//                game.scores.playerScores
//                    .find { it.player.id == playerId }
//                    ?.score ?: 0L
//            }
//        } ?: 0L
//
//private fun getAverageSessionScoreForPlayer(playerId: Int, games: List<GameEntity>): Double =
//    games.groupBy { getSessionStartForDateTime(it.date) }
//        .values.mapNotNull { sessionGames ->
//            sessionGames
//                .sumOf { game ->
//                    game.scores.playerScores
//                        .find { it.player.id == playerId }
//                        ?.score ?: 0L
//                }
//                .let { if (it > 0) it else null }
//        }
//        .let { if (it.isEmpty()) 0.0 else it.average() }
//
//private fun getSessionStartForDateTime(dateTime: LocalDateTime): LocalDateTime =
//    if (dateTime.hour >= 4) {
//        dateTime.toLocalDate().atTime(4, 0)
//    } else {
//        dateTime.toLocalDate().minusDays(1).atTime(4, 0)
//    }