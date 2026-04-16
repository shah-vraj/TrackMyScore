package com.vraj.trackmyscore.ui.base

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vraj.trackmyscore.ui.theme.BorderWhite
import com.vraj.trackmyscore.ui.theme.GlassWhite

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    brush: Brush? = null,
    cornerRadius: Dp = 12.dp,
    textColor: Color = Color.White,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    onButtonLongClicked: @Composable () -> Unit = { },
    text: String,
    onButtonClicked: @Composable () -> Unit
) {
    val triggerButtonClick = remember { mutableStateOf(false) }
    val triggerButtonLongClick = remember { mutableStateOf(false) }
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
            .height(54.dp)
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(cornerRadius))
            .then(
                if (brush != null) Modifier.background(brush)
                else Modifier.background(if (backgroundColor == Color.Transparent) GlassWhite else backgroundColor)
            )
            .border(1.dp, BorderWhite, RoundedCornerShape(cornerRadius))
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { triggerButtonClick.value = true },
                onLongClick = { triggerButtonLongClick.value = true }
            )
    ) {
        Text(
            text = text,
            style = textStyle,
            lineHeight = 20.sp,
            color = textColor
        )
    }

    if (triggerButtonClick.value) {
        onButtonClicked()
        triggerButtonClick.value = false
    }

    if (triggerButtonLongClick.value) {
        onButtonLongClicked()
        triggerButtonLongClick.value = false
    }
}