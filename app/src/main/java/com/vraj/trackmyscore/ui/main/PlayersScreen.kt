package com.vraj.trackmyscore.ui.main

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vraj.trackmyscore.R
import com.vraj.trackmyscore.model.AlertDialogData
import com.vraj.trackmyscore.model.SelectablePlayer
import com.vraj.trackmyscore.ui.base.BaseButton
import com.vraj.trackmyscore.ui.base.BaseConfirmationDialog
import com.vraj.trackmyscore.ui.base.BaseTextField
import com.vraj.trackmyscore.ui.theme.Green
import com.vraj.trackmyscore.util.MainScreen
import com.vraj.trackmyscore.viewmodel.MainViewModel
import com.vraj.trackmyscore.viewmodel.MainViewModel.Companion.INVALID_ERROR_MESSAGE_ID

@Composable
fun PlayersScreen(
    navHostController: NavHostController,
    mainViewModel: MainViewModel
) {
    val players by mainViewModel.players.collectAsState()
    val errorMessageId by mainViewModel.errorMessage.collectAsState()
    var newPlayerName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    HandleAlertDialog(mainViewModel)

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            BaseButton(
                text = stringResource(R.string.txt_leaderboard),
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 10.dp),
                onButtonClicked = { navHostController.navigate(MainScreen.LeaderboardScreen.route) }
            )

            BaseButton(
                text = stringResource(R.string.txt_stats),
                modifier = Modifier
                    .weight(0.4f)
                    .padding(horizontal = 10.dp)
            ) {
                navHostController.navigate(MainScreen.StatsScreen.route)
            }
        }

        if (players.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxHeight(0.7f)
            ) {
                Text(
                    text = stringResource(R.string.txt_add_players_to_start_game),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            PlayersList(
                players = players,
                mainViewModel = mainViewModel,
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .fillMaxWidth()
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BaseTextField(
                textFieldValue = newPlayerName,
                onValueChanged = { newPlayerName = it },
                placeholder = stringResource(R.string.txt_placeholder_player_name),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                ),
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Green)
                    .padding(horizontal = 15.dp)
            ) {
                BaseButton(
                    text = stringResource(id = R.string.txt_add),
                    backgroundColor = Green
                ) {
                    mainViewModel.addPlayerIfExists(newPlayerName.trim()) {
                        newPlayerName = ""
                        focusManager.clearFocus()
                    }
                }
            }
        }

        if (errorMessageId != INVALID_ERROR_MESSAGE_ID) {
            Text(
                text = stringResource(id = errorMessageId),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Red
            )
        }

        BaseButton(text = stringResource(R.string.txt_start_game)) {
            if (players.map { it.isSelected }.count() >= 2) {
                navHostController.navigate(MainScreen.GameScreen.route)
                return@BaseButton
            }

            Toast.makeText(
                context,
                stringResource(R.string.txt_please_select_at_least_2_player),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
private fun PlayersList(
    players: List<SelectablePlayer>,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(modifier = modifier) {
        items(players) {
            PlayerItem(
                mainViewModel = mainViewModel,
                selectablePlayer = it,
                onSelectPlayer = { isSelected ->
                    mainViewModel.setSelection(it.player.id, isSelected)
                },
                getStringResource = { id -> context.getString(id) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlayerItem(
    mainViewModel: MainViewModel,
    selectablePlayer: SelectablePlayer,
    onSelectPlayer: (Boolean) -> Unit,
    getStringResource: (Int) -> String
) {
    val player = selectablePlayer.player

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
                .combinedClickable(
                    onLongClick = {
                        val alertDialogData = AlertDialogData(
                            title = getStringResource(R.string.txt_remove_player),
                            message = getStringResource(R.string.txt_remove_player_message),
                            subMessage = player.name,
                            onConfirmAction = {
                                mainViewModel.apply {
                                    removePlayer(player.id)
                                    showAlertDialog(null)
                                }
                            }
                        )
                        mainViewModel.showAlertDialog(alertDialogData)
                    },
                    onClick = { onSelectPlayer(!selectablePlayer.isSelected) }
                )
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

            if (selectablePlayer.isSelected) {
                Image(
                    painter = painterResource(id = R.drawable.ic_tick),
                    contentDescription = "Player selection",
                    modifier = Modifier.padding(5.dp)
                )
            }
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
fun HandleAlertDialog(viewModel: MainViewModel) {
    val alertDialogData by viewModel.showAlertDialog.collectAsState()

    alertDialogData?.let {
        BaseConfirmationDialog(
            title = it.title,
            message = it.message,
            subMessage = it.subMessage,
            onConfirm = { it.onConfirmAction() },
            onCancel = { viewModel.showAlertDialog(null) }
        )
    }
}

@Composable
fun ShowToast(viewModel: MainViewModel) {
    val toast by viewModel.toastMessage.collectAsState()

    toast?.let {
        Toast.makeText(LocalContext.current, stringResource(it), Toast.LENGTH_SHORT).show()
        viewModel.onToastShown()
    }
}
