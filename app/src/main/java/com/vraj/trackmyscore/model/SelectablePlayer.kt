package com.vraj.trackmyscore.model

import com.vraj.trackmyscore.data.entity.PlayerEntity

data class SelectablePlayer(
    val player: PlayerEntity,
    var isSelected: Boolean
)
