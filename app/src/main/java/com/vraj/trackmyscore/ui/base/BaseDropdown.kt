package com.vraj.trackmyscore.ui.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vraj.trackmyscore.R
import com.vraj.trackmyscore.ui.theme.BorderWhite
import com.vraj.trackmyscore.ui.theme.GlassWhite

@Composable
fun <T> BaseDropdown(
    selectedItemName: String,
    items: List<T>,
    getItemName: (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation",
        animationSpec = tween(durationMillis = 150)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = 150)) // Smoothly animates the parent's height change
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GlassWhite)
                .border(1.dp, BorderWhite, RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Custom interaction handled by the box
                ) { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedItemName.ifEmpty { "Select" },
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_drop_down),
                    contentDescription = "Expand",
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotation)
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(animationSpec = tween(150)) + expandVertically(animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(150)) + shrinkVertically(animationSpec = tween(150))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1A2E).copy(alpha = 0.95f))
                    .border(1.dp, BorderWhite.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            ) {
                items.forEachIndexed { index, item ->
                    val itemName = getItemName(item)
                    val isSelected = itemName == selectedItemName
                    
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onItemSelected(item)
                                    expanded = false
                                }
                                .padding(16.dp)
                        ) {
                            Text(
                                text = itemName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color(0xFFFFD700) else Color.White
                            )
                        }
                        if (index < items.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = Color.White.copy(alpha = 0.05f)
                            )
                        }
                    }
                }
            }
        }
    }
}