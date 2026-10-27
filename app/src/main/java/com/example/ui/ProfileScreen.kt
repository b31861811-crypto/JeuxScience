package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.GameHistory
import com.example.data.GameRepository
import com.example.data.UserStats

@Composable
fun ProfileScreen(
    repository: GameRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by repository.userStats.collectAsStateWithLifecycle(initialValue = UserStats())
    val history by repository.recentHistory.collectAsStateWithLifecycle(initialValue = emptyList())

    val xpForCurrentLevel = stats.xp % 250
    val progressToNextLevel = (xpForCurrentLevel / 250f).coerceIn(0f, 1f)

    val rankTitle = when {
        stats.level >= 10 -> "🏆 Grand Maître des Sciences"
        stats.level >= 7 -> "⚡ Ingénieur Élite"
        stats.level >= 4 -> "🏎️ Pilote Chercheur"
        stats.level >= 2 -> "⚽ Athlète Scientifique"
        else -> "🌱 Apprenti des Sciences"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1D))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color(0x33FFFFFF), CircleShape)
                    .testTag("profile_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Profil & Trophées",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    "Votre progression d'apprentissage",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Level & Rank Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF2563EB),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(34.dp))
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("NIVEAU ${stats.level}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text(rankTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Progression XP : ${stats.xp} XP total", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("$xpForCurrentLevel / 250 XP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progressToNextLevel },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = Color(0xFF38BDF8),
                            trackColor = Color(0xFF1E293B)
                        )
                    }
                }
            }

            // Subject Mastery Breakdown
            item {
                Text(
                    "MAÎTRISE PAR DISCIPLINE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Mécanique
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF111827),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF8A00)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚙️", fontSize = 24.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Mécanique", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${stats.mecaScore} pts", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF8A00))
                        }
                    }

                    // Programmation
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF111827),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00D2FF)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💻", fontSize = 24.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Programmation", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${stats.progScore} pts", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF00D2FF))
                        }
                    }

                    // Maths
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF111827),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📐", fontSize = 24.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Maths", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${stats.mathScore} pts", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                        }
                    }
                }
            }

            // Gameplay Stats
            item {
                Text(
                    "STATISTIQUES DE JEU",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color(0xFFFF8A00), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Courses disputées", color = Color.White, fontSize = 13.sp)
                            }
                            Text("${stats.totalRaces}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Meilleur score en course", color = Color.White, fontSize = 13.sp)
                            }
                            Text("${stats.bestRaceScore} PTS", fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F), fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Buts marqués au foot", color = Color.White, fontSize = 13.sp)
                            }
                            Text("${stats.totalGoals} buts sur ${stats.totalShots} tirs", fontWeight = FontWeight.Bold, color = Color(0xFF10B981), fontSize = 13.sp)
                        }
                    }
                }
            }

            // Recent Game History
            item {
                Text(
                    "HISTORIQUE DES ÉPREUVES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
            }

            if (history.isEmpty()) {
                item {
                    Text(
                        "Aucune épreuve terminée pour le moment. Lancez une course ou un match de foot pour vous entraîner !",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(history, key = { it.id }) { item ->
                    HistoryItemCard(item = item)
                }
            }

            item {
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun HistoryItemCard(item: GameHistory) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF111827),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (item.gameMode == "RACE") "🏎️" else "⚽",
                    fontSize = 20.sp
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        if (item.gameMode == "RACE") "Course Grand Prix" else "Tir au but / Coup franc",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        item.details,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "+${item.xpEarned} XP",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF10B981),
                    fontSize = 13.sp
                )
                Text(
                    "${item.score} pts",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
