package com.vraj.trackmyscore.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vraj.trackmyscore.R
import com.vraj.trackmyscore.data.entity.PlayerEntity
import com.vraj.trackmyscore.util.LeaderboardType
import com.vraj.trackmyscore.util.extension.toStringByLimitingDecimalDigits
import com.vraj.trackmyscore.viewmodel.MainViewModel

@Composable
fun LeaderboardScreen(
    navHostController: NavHostController,
    mainViewModel: MainViewModel
) {
    val players by mainViewModel.players.collectAsState()
    val selectedLeaderboardType by mainViewModel.selectedLeaderboardType.collectAsState()

    LaunchedEffect(true) {
        mainViewModel.updateData()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(40.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back",
                modifier = Modifier
                    .clickable { navHostController.popBackStack() }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(id = R.string.txt_leaderboard),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        LeaderboardDropdownItem(
            selectedItem = selectedLeaderboardType,
            onItemSelected = { mainViewModel.setSelectedLeaderboardType(it) }
        )

        when(selectedLeaderboardType) {
            LeaderboardType.MOST_RUNS -> LazyColumn {
                players.map { it.player }
                    .sortedByDescending { it.runs }
                    .let { sortedPlayers ->
                        items(sortedPlayers, key = { it.id }) { player ->
                            PlayerItem(player = player, value = player.runs.toDouble())
                        }
                    }
            }

            LeaderboardType.AVERAGE -> LazyColumn {
                players.map { it.player }
                    .sortedByDescending { it.average }
                    .let { sortedPlayers ->
                        items(sortedPlayers, key = { it.id }) { player ->
                            PlayerItem(player = player, value = player.average, false)
                        }
                    }
            }

            LeaderboardType.HIGHEST_INDIVIDUAL -> LazyColumn {
                players.map { it.player }
                    .sortedByDescending { it.highestScore }
                    .let { sortedPlayers ->
                        items(sortedPlayers, key = { it.id }) { player ->
                            PlayerItem(player = player, value = player.highestScore.toDouble())
                        }
                    }
            }

            LeaderboardType.MOST_WICKETS -> LazyColumn {
                players.map { it.player }
                    .sortedByDescending { it.wickets }
                    .let { sortedPlayers ->
                        items(sortedPlayers, key = { it.id }) { player ->
                            PlayerItem(player = player, value = player.wickets.toDouble())
                        }
                    }
            }

            LeaderboardType.MOST_CATCHES -> LazyColumn {
                players.map { it.player }
                    .sortedByDescending { it.catches }
                    .let { sortedPlayers ->
                        items(sortedPlayers, key = { it.id }) { player ->
                            PlayerItem(player = player, value = player.catches.toDouble())
                        }
                    }
            }
        }
    }
}

@Composable
private fun PlayerItem(player: PlayerEntity, value: Double, canBeLongValue: Boolean = true) {
    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(player.color)
            ) {
                Text(
                    text = player.initials,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }

            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = if (canBeLongValue)
                    value.toLong().toString()
                else value.toStringByLimitingDecimalDigits(2),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray)
        )
    }
}

@Composable
private fun LeaderboardDropdownItem(
    selectedItem: LeaderboardType,
    onItemSelected: (LeaderboardType) -> Unit
) {
    var isDropDownExpanded by remember { mutableStateOf(false) }
    val dropdownList by remember { mutableStateOf(LeaderboardType.getLeaderboardTypeList()) }

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
                    text = selectedItem.title,
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
                dropdownList.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(text = type.title) },
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