package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 1,
    val totalRaces: Int = 0,
    val bestRaceScore: Int = 0,
    val totalGoals: Int = 0,
    val totalShots: Int = 0,
    val mecaScore: Int = 0,
    val progScore: Int = 0,
    val mathScore: Int = 0,
    val selectedCarSkin: String = "formula_red",
    val selectedBallSkin: String = "classic"
)

@Entity(tableName = "game_history")
data class GameHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameMode: String, // "RACE" or "FOOT"
    val subject: String,  // "MECANIQUE", "PROGRAMMATION", "MATHS", "MIXTE"
    val score: Int,
    val xpEarned: Int,
    val details: String,  // e.g. "5 buts marqués", "Vitesse max 280 km/h"
    val timestamp: Long = System.currentTimeMillis()
)
