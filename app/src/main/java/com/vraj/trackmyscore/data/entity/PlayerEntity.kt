package com.vraj.trackmyscore.data.entity

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vraj.trackmyscore.ui.theme.InitialsFour
import com.vraj.trackmyscore.ui.theme.InitialsOne
import com.vraj.trackmyscore.ui.theme.InitialsThree
import com.vraj.trackmyscore.ui.theme.InitialsTwo
import com.vraj.trackmyscore.ui.theme.InitialsZero
import com.vraj.trackmyscore.util.extension.toStringByLimitingDecimalDigits

@Entity(tableName = "Player")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("runs")
    val runs: Long = 0,

    @ColumnInfo("outs")
    val outs: Long = 0,

    @ColumnInfo("wickets")
    val wickets: Long = 0,

    @ColumnInfo("catches")
    val catches: Long = 0,

    @ColumnInfo("highest_score")
    val highestScore: Long = 0
) {
    val average: Double
        get() = if (outs == 0L) runs.toDouble() else runs.toDouble() / outs

    val averageString: String
        get() = average.toStringByLimitingDecimalDigits(2)

    val mvpScore: Double
        get() = runs * 2 + average * 5 + wickets * 10 + catches * 5

    val mvpScoreString: String
        get() = mvpScore.toStringByLimitingDecimalDigits(2)

    val initials: String
        get() {
            val parts = name.split(" ")
            return when {
                parts.size > 1 -> parts[0].first().uppercase() + parts[1].first().uppercase()
                name.length > 1 -> parts[0].first().uppercase() + parts[0][1].uppercase()
                else -> parts[0].first().uppercase()
            }
        }

    val color: Color
        get() {
            val flag = initials[0].code % 5
            return when(flag) {
                0 -> InitialsZero
                1 -> InitialsOne
                2 -> InitialsTwo
                3 -> InitialsThree
                4 -> InitialsFour
                else -> InitialsZero
            }
        }

    companion object {
        val dummy = PlayerEntity(-1, "None")
    }
}