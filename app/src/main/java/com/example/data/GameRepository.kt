package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(
    private val userStatsDao: UserStatsDao,
    private val gameHistoryDao: GameHistoryDao
) {
    val userStats: Flow<UserStats> = userStatsDao.getUserStats().map { it ?: UserStats() }
    val recentHistory: Flow<List<GameHistory>> = gameHistoryDao.getRecentHistory()

    suspend fun recordRaceResult(
        score: Int,
        xpEarned: Int,
        subject: String,
        mecaBonus: Int,
        progBonus: Int,
        mathBonus: Int,
        details: String
    ) {
        val current = userStatsDao.getUserStatsOnce() ?: UserStats()
        val newXp = current.xp + xpEarned
        val newLevel = 1 + (newXp / 250) // Each 250 XP is 1 level up
        val updated = current.copy(
            xp = newXp,
            level = newLevel,
            totalRaces = current.totalRaces + 1,
            bestRaceScore = maxOf(current.bestRaceScore, score),
            mecaScore = current.mecaScore + mecaBonus,
            progScore = current.progScore + progBonus,
            mathScore = current.mathScore + mathBonus
        )
        userStatsDao.insertOrUpdate(updated)
        gameHistoryDao.insert(
            GameHistory(
                gameMode = "RACE",
                subject = subject,
                score = score,
                xpEarned = xpEarned,
                details = details
            )
        )
    }

    suspend fun recordFootResult(
        goals: Int,
        shots: Int,
        xpEarned: Int,
        subject: String,
        mecaBonus: Int,
        progBonus: Int,
        mathBonus: Int,
        details: String
    ) {
        val current = userStatsDao.getUserStatsOnce() ?: UserStats()
        val newXp = current.xp + xpEarned
        val newLevel = 1 + (newXp / 250)
        val updated = current.copy(
            xp = newXp,
            level = newLevel,
            totalGoals = current.totalGoals + goals,
            totalShots = current.totalShots + shots,
            mecaScore = current.mecaScore + mecaBonus,
            progScore = current.progScore + progBonus,
            mathScore = current.mathScore + mathBonus
        )
        userStatsDao.insertOrUpdate(updated)
        gameHistoryDao.insert(
            GameHistory(
                gameMode = "FOOT",
                subject = subject,
                score = goals,
                xpEarned = xpEarned,
                details = details
            )
        )
    }

    suspend fun updateCustomization(carSkin: String? = null, ballSkin: String? = null) {
        val current = userStatsDao.getUserStatsOnce() ?: UserStats()
        val updated = current.copy(
            selectedCarSkin = carSkin ?: current.selectedCarSkin,
            selectedBallSkin = ballSkin ?: current.selectedBallSkin
        )
        userStatsDao.insertOrUpdate(updated)
    }
}
