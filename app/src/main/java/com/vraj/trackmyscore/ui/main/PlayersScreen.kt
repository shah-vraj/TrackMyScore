package com.vraj.trackmyscore.ui.main

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vraj.trackmyscore.R
import com.vraj.trackmyscore.model.AlertDialogData
import com.vraj.trackmyscore.model.SelectablePlayer
import com.vraj.trackmyscore.ui.base.BaseButton
import com.vraj.trackmyscore.ui.base.BaseConfirmationDialog
import com.vraj.trackmyscore.ui.base.BaseTextField
import com.vraj.trackmyscore.ui.theme.BorderWhite
import com.vraj.trackmyscore.ui.theme.GlassWhite
import com.vraj.trackmyscore.ui.theme.GradientStadium
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
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = "Dressing Room",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = Color.White,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BaseButton(
                text = stringResource(R.string.txt_leaderboard),
                modifier = Modifier.weight(1f),
                onButtonClicked = { navHostController.navigate(MainScreen.LeaderboardScreen.route) }
            )

            BaseButton(
                text = stringResource(R.string.txt_stats),
                modifier = Modifier.weight(1f)
            ) {
                navHostController.navigate(MainScreen.StatsScreen.route)
            }
        }

        if (players.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassWhite)
                    .border(1.dp, BorderWhite, RoundedCornerShape(20.dp))
            ) {
                Text(
                    text = stringResource(R.string.txt_add_players_to_start_game),
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            PlayersGrid(
                players = players,
                mainViewModel = mainViewModel,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
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

                BaseButton(
                    text = stringResource(id = R.string.txt_add),
                    modifier = Modifier.width(80.dp),
                    brush = Brush.horizontalGradient(GradientStadium)
                ) {
                    mainViewModel.addPlayerIfExists(newPlayerName.trim()) {
                        newPlayerName = ""
                        focusManager.clearFocus()
                    }
                }
            }

            if (errorMessageId != INVALID_ERROR_MESSAGE_ID) {
                Text(
                    text = stringResource(id = errorMessageId),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFF4444),
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
            }
        }

        BaseButton(
            text = stringResource(R.string.txt_start_game),
            brush = Brush.horizontalGradient(listOf(Color(0xFFDD600B), Color(0xFFFF8C42)))
        ) {
            if (players.map { it.isSelected }.count { it } >= 2) {
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
private fun PlayersGrid(
    players: List<SelectablePlayer>,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(5.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(
            items = players,
            key = { it.player.id }
        ) {
            PlayerCard(
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
private fun PlayerCard(
    mainViewModel: MainViewModel,
    selectablePlayer: SelectablePlayer,
    onSelectPlayer: (Boolean) -> Unit,
    getStringResource: (Int) -> String
) {
    val player = selectablePlayer.player
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selectablePlayer.isSelected) Color.White.copy(alpha = 0.15f) else GlassWhite)
            .border(
                width = if (selectablePlayer.isSelected) 2.dp else 1.dp,
                color = if (selectablePlayer.isSelected) Color.White else BorderWhite,
                shape = RoundedCornerShape(16.dp)
            )
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
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
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(player.color.copy(alpha = 0.8f))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            ) {
                Text(
                    text = player.initials,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Text(
                text = player.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = Color.White,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            if (selectablePlayer.isSelected) {
                Image(
                    painter = painterResource(id = R.drawable.ic_tick),
                    contentDescription = "Selected",
                    modifier = Modifier
                        .size(24.dp)
                        .background(Green, CircleShape)
                        .padding(4.dp)
                )
            }
        }
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
