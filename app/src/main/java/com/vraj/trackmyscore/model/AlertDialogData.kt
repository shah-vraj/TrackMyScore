package com.vraj.trackmyscore.model

data class AlertDialogData(
    val title: String,
    val message: String,
    val subMessage: String,
    val onConfirmAction: () -> Unit
)
