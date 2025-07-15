package com.vraj.trackmyscore.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vraj.trackmyscore.R
import com.vraj.trackmyscore.data.entity.PlayerEntity
import com.vraj.trackmyscore.model.AlertDialogData
import com.vraj.trackmyscore.ui.base.BaseButton
import com.vraj.trackmyscore.ui.base.BaseTextField
import com.vraj.trackmyscore.util.AppSharedPreferences.Companion.INVALID_ID
import com.vraj.trackmyscore.util.BatterAction.DIRECT_RUNS
import com.vraj.trackmyscore.util.BatterAction.WK_CATCH
import com.vraj.trackmyscore.viewmodel.MainViewModel

@Composable
fun GameScreen(
    navHostController: NavHostController,
    viewModel: MainViewModel
) {
    val scrollState = rememberScrollState()
    val players by viewModel.inGamePlayers.collectAsState(listOf())
    val currentBatter by viewModel.currentBatter.collectAsState()
    val currentBatterRuns by viewModel.currentBatterRuns.collectAsState()
    val selectedBatsman by viewModel.selectedBatsman.collectAsState()
    val selectedBowler by viewModel.selectedBowler.collectAsState()
    val selectedCatcher by viewModel.selectedCatcher.collectAsState()
    val batterActions by viewModel.batterActions.collectAsState()
    val selectedDirectRuns by viewModel.selectedDirectRuns.collectAsState()

    LaunchedEffect(true) { viewModel.fetchCurrentBatter() }

    HandleAlertDialog(viewModel)
    ShowToast(viewModel)

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
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
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            if (currentBatter.id != INVALID_ID) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentBatterRuns.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )

                    Text(
                        text = currentBatter.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (currentBatter.id == INVALID_ID) {
                    PlayerDropDownItem(
                        text = stringResource(R.string.txt_select_batsman),
                        selectedPlayer = selectedBatsman,
                        players = players,
                        onItemSelected = { viewModel.setSelectedBatsman(it) }
                    )
                }

                PlayerDropDownItem(
                    text = stringResource(R.string.txt_select_bowler),
                    selectedPlayer = selectedBowler,
                    players = players.filter { it.name != selectedBatsman.name },
                    onItemSelected = { viewModel.setSelectedBowler(it) }
                )
            }

            if (currentBatter.id == INVALID_ID) {
                BaseButton(text = stringResource(R.string.txt_start_innings)) {
                    viewModel.startInnings()
                }
            }

            if (currentBatter.id != INVALID_ID) {
                LazyHorizontalStaggeredGrid(
                    rows = StaggeredGridCells.Fixed(3),
                    horizontalItemSpacing = 10.dp,
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    items(batterActions) {
                        Text(
                            text = it.first.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (it.second)
                                        MaterialTheme.colorScheme.secondary
                                    else
                                        Color.Transparent
                                )
                                .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                                .padding(15.dp)
                                .clickable(remember { MutableInteractionSource() }, null) {
                                    viewModel.selectBatterAction(it.first)
                                }
                        )
                    }
                }
            }

            if (batterActions.find { it.first == WK_CATCH }?.second == true) {
                PlayerDropDownItem(
                    text = stringResource(R.string.txt_select_catcher),
                    selectedPlayer = selectedCatcher,
                    players = players.filter { it.name != selectedBatsman.name },
                    onItemSelected = { viewModel.setSelectedCatcher(it) }
                )
            }

            if (batterActions.find { it.first == DIRECT_RUNS }?.second == true) {
                BaseTextField(
                    textFieldValue = selectedDirectRuns,
                    onValueChanged = { viewModel.setSelectedDirectRuns(it) },
                    placeholder = stringResource(R.string.txt_enter_direct_runs_to_add),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            if (currentBatter.id != INVALID_ID) {
                BaseButton(text = stringResource(R.string.txt_record_action)) {
                    val alertDialogData = AlertDialogData(
                        title = stringResource(R.string.txt_title_record_action),
                        message = stringResource(R.string.txt_message_record_action),
                        subMessage = "",
                        onConfirmAction = {
                            viewModel.recordInnings()
                            viewModel.showAlertDialog(null)
                        }
                    )
                    viewModel.showAlertDialog(alertDialogData)
                }
            }
        }
    }
}

@Composable
private fun PlayerDropDownItem(
    text: String,
    selectedPlayer: PlayerEntity,
    players: List<PlayerEntity>,
    onItemSelected: (PlayerEntity) -> Unit
) {
    var isDropDownExpanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )

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
                players.forEach { playerEntity ->
                    DropdownMenuItem(
                        text = { Text(text = playerEntity.name) },
                        onClick = {
                            isDropDownExpanded = false
                            onItemSelected(playerEntity)
                        }
                    )
                }
            }
        }
    }
}