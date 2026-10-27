package com.example.game.foot

import androidx.compose.ui.graphics.Color
import com.example.model.ScienceChallenge
import com.example.model.Subject

enum class ShotOutcome {
    NONE,
    GOAL,           // BUT !
    KEEPER_SAVED,   // Arrêt du gardien !
    CROSSBAR_POST,  // Poteau ou barre !
    MISSED_WIDE,    // À côté
    MISSED_OVER,    // Au-dessus
    HIT_WALL        // Contré par le mur !
}

enum class KeeperDecision(val label: String, val targetX: Float, val targetY: Float) {
    CENTER("Reste au centre", 0f, 1.0f),
    DIVE_LEFT_LOW("Plongeon ras du poteau gauche", -2.6f, 0.4f),
    DIVE_LEFT_HIGH("Envolée lucarne gauche", -2.8f, 2.1f),
    DIVE_RIGHT_LOW("Plongeon ras du poteau droit", 2.6f, 0.4f),
    DIVE_RIGHT_HIGH("Envolée lucarne droite", 2.8f, 2.1f),
    JUMP_CENTER_HIGH("Claquette sous la barre", 0f, 2.2f)
}

data class FootGameState(
    val isFreeKick: Boolean = false, // false = Penalty (11m), true = Coup Franc (22m avec mur)
    val distanceMeters: Float = 11f,
    // Kicker setup
    val aimAngleDeg: Float = 18f,    // Vertical angle theta (10° to 45°)
    val aimDirectionDeg: Float = 0f, // Horizontal aim (-20° left to +20° right)
    val kickPowerMs: Float = 24f,    // Initial velocity v0 (14 to 34 m/s)
    val spinRpm: Float = 0f,         // Magnus spin (-30 rad/s left to +30 rad/s right)

    // Ball 3D position (in meters: x = lateral [-3.66..3.66], y = height [0..2.44], z = distance to goal [11..0])
    val ballX: Float = 0f,
    val ballY: Float = 0f,
    val ballZ: Float = 11f,
    val isBallInFlight: Boolean = false,

    // Goalkeeper position
    val keeperX: Float = 0f,
    val keeperY: Float = 1.0f,
    val keeperDecision: KeeperDecision = KeeperDecision.CENTER,
    val keeperAlgorithmSnippet: String = "",

    // Game stats
    val scoreGoals: Int = 0,
    val totalShots: Int = 0,
    val outcome: ShotOutcome = ShotOutcome.NONE,
    val outcomeMessage: String = "Ajustez l'angle, la puissance et le spin !",
    val subject: Subject = Subject.MIXTE,
    val activeChallenge: ScienceChallenge? = null,
    val hasPredictiveVision: Boolean = false,
    val streak: Int = 0
)
