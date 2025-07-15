package com.vraj.trackmyscore.util

import android.content.Context
import androidx.core.content.edit
import com.vraj.trackmyscore.R
import com.vraj.trackmyscore.util.AppSharedPreferences.Companion.INVALID_ID
import com.vraj.trackmyscore.util.Constants.CURRENT_BATTER_ID_KEY
import com.vraj.trackmyscore.util.Constants.CURRENT_BATTER_RUNS_KEY

interface AppSharedPreferences {

    fun getCurrentBatterId(): Int

    fun setCurrentBatterId(id: Int)

    fun getCurrentBatterRuns(): Long

    fun setCurrentBatterRuns(runs: Long)

    companion object {
        const val INVALID_ID = -1
    }
}

class AppSharedPreferencesImpl(context: Context) : AppSharedPreferences {
    private val prefs =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    override fun getCurrentBatterId(): Int =
        prefs.getInt(CURRENT_BATTER_ID_KEY, INVALID_ID)

    override fun setCurrentBatterId(id: Int) {
        prefs.edit { putInt(CURRENT_BATTER_ID_KEY, id) }
    }

    override fun getCurrentBatterRuns(): Long =
        prefs.getLong(CURRENT_BATTER_RUNS_KEY, 0)

    override fun setCurrentBatterRuns(runs: Long) {
        prefs.edit { putLong(CURRENT_BATTER_RUNS_KEY, runs) }
    }
}