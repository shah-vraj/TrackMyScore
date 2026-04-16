package com.vraj.trackmyscore.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val colorScheme = darkColorScheme(
    primary = DarkBlue,
    onPrimary = Color.White,
    secondary = Orange,
    onSecondary = Color.White
)

@Composable
fun TrackMyScoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PoppinsTypography,
        content = content
    )
}