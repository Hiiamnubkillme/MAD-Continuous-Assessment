// Basic/HighScoreManager.kt
package com.example.whackamole.basic

import android.content.Context
import android.content.SharedPreferences

class HighScoreManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("WhackAMolePrefs", Context.MODE_PRIVATE)

    fun saveHighScore(score: Int) {
        prefs.edit().putInt("high_score", score).apply()
    }

    fun getHighScore(): Int {
        return prefs.getInt("high_score", 0)
    }
}