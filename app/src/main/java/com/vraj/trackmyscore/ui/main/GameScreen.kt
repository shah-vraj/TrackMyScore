package com.vraj.trackmyscore.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vraj.trackmyscore.R
import com.vraj.trackmyscore.data.entity.MatchPlayerEntity
import com.vraj.trackmyscore.data.entity.PlayerEntity
import com.vraj.trackmyscore.model.AlertDialogData
import com.vraj.trackmyscore.ui.base.BaseButton
import com.vraj.trackmyscore.ui.base.BaseDropdown
import com.vraj.trackmyscore.ui.base.BaseTextField
import com.vraj.trackmyscore.ui.theme.BorderWhite
import com.vraj.trackmyscore.ui.theme.GlassWhite
import com.vraj.trackmyscore.ui.theme.GradientStadium
import com.vraj.trackmyscore.ui.theme.Orange
import com.vraj.trackmyscore.util.AppSharedPreferences.Companion.INVALID_ID
import com.vraj.trackmyscore.util.BatterAction
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
    val battingOrder by viewModel.battingOrderList.collectAsState()
    val roundSummary by viewModel.roundSummary.collectAsState()

    LaunchedEffect(true) { viewModel.fetchCurrentBatter() }

    HandleAlertDialog(viewModel)
    ShowToast(viewModel)

    Box(modifier = Modifier.fillMaxSize()) {
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
                    text = "Live Match",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))
                
                Box(modifier = Modifier.size(32.dp)) // Anchor for centering title
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Batting Order Ticker
                if (battingOrder.isNotEmpty()) {
                    BattingOrderTicker(battingOrder)
                }

                if (currentBatter.id != INVALID_ID) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(GlassWhite)
                            .border(1.dp, BorderWhite, RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = currentBatter.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White.copy(alpha = 0.7f)
                            )

                            Text(
                                text = currentBatterRuns.toString(),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 80.sp
                                ),
                                color = Color.White
                            )

                            Text(
                                text = "RUNS",
                                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 4.sp),
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (currentBatter.id == INVALID_ID) {
                        BattingOrderReorderSection(
                            battingOrder = battingOrder,
                            onShuffle = { viewModel.shuffleMatchOrder() },
                            onMove = { from, to -> viewModel.moveMatchPlayer(from, to) }
                        )

                        BaseDropdown(
                            selectedItemName = selectedBatsman.name,
                            items = players,
                            getItemName = { it.name },
                            onItemSelected = { viewModel.setSelectedBatsman(it) },
                            label = stringResource(R.string.txt_select_batsman)
                        )
                    }

                    BaseDropdown(
                        selectedItemName = selectedBowler.name,
                        items = players.filter { it.name != selectedBatsman.name },
                        getItemName = { it.name },
                        onItemSelected = { viewModel.setSelectedBowler(it) },
                        label = stringResource(R.string.txt_select_bowler)
                    )
                }

                if (currentBatter.id == INVALID_ID) {
                    BaseButton(
                        text = stringResource(R.string.txt_start_innings),
                        brush = Brush.horizontalGradient(GradientStadium)
                    ) {
                        viewModel.startInnings()
                    }
                }

                if (currentBatter.id != INVALID_ID) {
                    Text(
                        text = "SELECT ACTION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.align(Alignment.Start).padding(start = 5.dp)
                    )

                    ActionGrid(
                        actions = batterActions,
                        onActionClick = { viewModel.selectBatterAction(it) }
                    )
                }

                if (batterActions.find { it.first == WK_CATCH }?.second == true) {
                    BaseDropdown(
                        selectedItemName = selectedCatcher.name,
                        items = players.filter { it.name != selectedBatsman.name },
                        getItemName = { it.name },
                        onItemSelected = { viewModel.setSelectedCatcher(it) },
                        label = stringResource(R.string.txt_select_catcher)
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
                    BaseButton(
                        text = stringResource(R.string.txt_record_action),
                        brush = Brush.horizontalGradient(listOf(Orange, Color(0xFFFF8C42)))
                    ) {
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

        // Round Summary Overlay
        AnimatedVisibility(
            visible = roundSummary != null,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 0.8f)
        ) {
            roundSummary?.let { summary ->
                RoundSummaryScreen(
                    summary = summary,
                    onDismiss = { viewModel.dismissRoundSummary() }
                )
            }
        }
    }
}

@Composable
private fun BattingOrderReorderSection(
    battingOrder: List<Triple<MatchPlayerEntity, PlayerEntity, Boolean>>,
    onShuffle: () -> Unit,
    onMove: (Int, Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Next Up Order",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Shuffle",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Orange,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onShuffle() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .background(Orange.copy(alpha = 0.1f))
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GlassWhite)
                .padding(12.dp)
        ) {
            val upcoming = battingOrder.filter { !it.third }
            if (upcoming.isEmpty()) {
                Text(
                    text = "No players remaining in this round",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            } else {
                upcoming.forEachIndexed { index, (_, player, _) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.width(24.dp)
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(player.color.copy(alpha = 0.8f))
                        ) {
                            Text(
                                text = player.initials,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Reorder buttons
                        Row {
                            if (index > 0) {
                                Text(
                                    text = "↑",
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clickable { 
                                            // Find global indices in the full battingOrder list
                                            val fromIdx = battingOrder.indexOfFirst { it.second.id == player.id }
                                            val toIdx = battingOrder.indexOfFirst { it.second.id == upcoming[index - 1].second.id }
                                            onMove(fromIdx, toIdx)
                                        }
                                )
                            }
                            if (index < upcoming.size - 1) {
                                Text(
                                    text = "↓",
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clickable { 
                                            val fromIdx = battingOrder.indexOfFirst { it.second.id == player.id }
                                            val toIdx = battingOrder.indexOfFirst { it.second.id == upcoming[index + 1].second.id }
                                            onMove(fromIdx, toIdx)
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BattingOrderTicker(
    order: List<Triple<MatchPlayerEntity, PlayerEntity, Boolean>>
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(10.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        order.forEach { (_, player, hasBatted) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(60.dp)
                    .alpha(if (hasBatted) 0.4f else 1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(player.color.copy(alpha = 0.8f))
                        .border(
                            width = if (!hasBatted) 2.dp else 0.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = player.initials,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Text(
                    text = player.name.split(" ").first(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RoundSummaryScreen(
    summary: List<Pair<PlayerEntity, Long>>,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable(enabled = false) {} // Consume clicks
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(GlassWhite)
                .border(1.dp, BorderWhite, RoundedCornerShape(32.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "Round Leaderboard",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                summary.forEachIndexed { index, (player, runs) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.width(30.dp)
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(player.color.copy(alpha = 0.8f))
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
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$runs Runs",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }

            BaseButton(
                text = "Next Round",
                brush = Brush.horizontalGradient(GradientStadium),
                onButtonClicked = { onDismiss() }
            )
        }
    }
}

@Composable
private fun ActionGrid(
    actions: List<Pair<BatterAction, Boolean>>,
    onActionClick: (BatterAction) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        actions.chunked(3).forEach { rowActions ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowActions.forEach { (action, isSelected) ->
                    ActionCard(
                        action = action,
                        isSelected = isSelected,
                        onClick = { onActionClick(action) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty slots if row is not full
                repeat(3 - rowActions.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    action: BatterAction,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(80.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color.White else GlassWhite)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.White else BorderWhite,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        Text(
            text = action.text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) Color(0xFF0A0A1A) else Color.White,
            textAlign = TextAlign.Center
        )
    }
}