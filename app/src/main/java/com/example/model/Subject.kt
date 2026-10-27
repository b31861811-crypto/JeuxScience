package com.example.model

import androidx.compose.ui.graphics.Color

enum class Subject(val title: String, val icon: String, val color: Color) {
    MECANIQUE("Mécanique", "⚙️", Color(0xFFFF8A00)),
    PROGRAMMATION("Programmation", "💻", Color(0xFF00D2FF)),
    MATHS("Mathématiques", "📐", Color(0xFF10B981)),
    MIXTE("Tous les sujets", "🏆", Color(0xFFA855F7));

    val description: String
        get() = when (this) {
            MECANIQUE -> "Forces, Cinématique, Énergie, Couple, Frottements"
            PROGRAMMATION -> "Algorithmes, Logique Booléenne, Boucles, Complexité"
            MATHS -> "Trigonométrie, Balistique, Vecteurs, Dérivées"
            MIXTE -> "Épreuves combinées Méca + Code + Maths"
        }
}

data class ScienceChallenge(
    val id: String,
    val subject: Subject,
    val title: String,
    val question: String,
    val codeSnippet: String? = null,
    val formulaSnippet: String? = null,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val gameBonusEffect: String // e.g. "+50 km/h Nitro Boost !", "Plein fouet dans la lucarne !"
)
