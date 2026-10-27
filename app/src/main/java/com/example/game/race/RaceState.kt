package com.example.game.race

import androidx.compose.ui.graphics.Color
import com.example.model.ScienceChallenge
import com.example.model.Subject

enum class RaceObstacleType {
    OIL_SLICK,      // Low friction mu
    CODE_BUG,       // Algorithmic obstacle
    GRAVEL_TRAP     // Deceleration
}

data class RaceObstacle(
    val id: Long,
    val lane: Int, // 0, 1, or 2
    var yPos: Float, // 0.0f (far horizon) to 1.0f (at player)
    val type: RaceObstacleType
)

enum class CollectibleType(val symbol: String, val label: String, val color: Color) {
    GEAR("⚙️", "+Énergie Méca", Color(0xFFFF8A00)),
    BIT("01", "+Code Turbo", Color(0xFF00D2FF)),
    PI("π", "+Boost Math", Color(0xFF10B981)),
    NITRO("⚡", "+Super Nitro", Color(0xFFFFEB3B))
}

data class RoadCollectible(
    val id: Long,
    val lane: Int,
    var yPos: Float,
    val type: CollectibleType
)

data class ActiveRaceChallenge(
    val challenge: ScienceChallenge,
    var distanceMet: Float
)

data class RaceGameState(
    val isRacing: Boolean = false,
    val isGameOver: Boolean = false,
    val currentLane: Float = 1.0f, // 0.0 (left), 1.0 (center), 2.0 (right)
    val targetLane: Int = 1,
    val speedKmh: Float = 120f,
    val maxSpeedKmh: Float = 260f,
    val distanceTraveled: Float = 0f,
    val targetDistance: Float = 3000f, // 3 km race
    val nitroEnergy: Float = 60f, // 0 to 100
    val isNitroActive: Boolean = false,
    val score: Int = 0,
    val gear: Int = 4,
    val rpm: Float = 5500f,
    val frictionMu: Float = 0.85f,
    val obstacles: List<RaceObstacle> = emptyList(),
    val collectibles: List<RoadCollectible> = emptyList(),
    val activeChallenge: ScienceChallenge? = null,
    val challengeStatus: ChallengeResultStatus = ChallengeResultStatus.NONE,
    val comboStreak: Int = 0,
    val subject: Subject = Subject.MIXTE,
    val carColor: Color = Color(0xFFE53935),
    val statusMessage: String = "Prêt au départ !"
)

enum class ChallengeResultStatus {
    NONE,
    CORRECT,
    WRONG
}
