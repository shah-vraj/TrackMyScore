package com.vraj.trackmyscore.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.vraj.trackmyscore.ui.theme.BorderWhite
import com.vraj.trackmyscore.ui.theme.GlassWhite

@Composable
fun BaseTextField(
    modifier: Modifier = Modifier,
    textFieldValue: String = "",
    onValueChanged: (String) -> Unit = { },
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions()
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GlassWhite)
            .border(1.dp, BorderWhite, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp)
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { onValueChanged(it) },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (textFieldValue.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.alpha(0.5f)
                        )
                    }
                    innerTextField()
                }
            },
            cursorBrush = SolidColor(Color.White),
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}