package com.example.game.race

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameRepository
import com.example.model.QuestionBank
import com.example.model.ScienceChallenge
import com.example.model.Subject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun RaceScreen(
    subject: Subject,
    repository: GameRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()

    var isRacing by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    var currentLane by remember { mutableFloatStateOf(1f) }
    var targetLane by remember { mutableIntStateOf(1) }
    val laneAnim = remember { Animatable(1f) }

    var speedKmh by remember { mutableFloatStateOf(120f) }
    var nitroEnergy by remember { mutableFloatStateOf(75f) }
    var isNitroActive by remember { mutableStateOf(false) }
    var distanceTraveled by remember { mutableFloatStateOf(0f) }
    val targetDistance = 2500f // 2.5 km finish line

    var score by remember { mutableIntStateOf(0) }
    var mecaCount by remember { mutableIntStateOf(0) }
    var progCount by remember { mutableIntStateOf(0) }
    var mathCount by remember { mutableIntStateOf(0) }
    var comboStreak by remember { mutableIntStateOf(0) }
    var statusMessage by remember { mutableStateOf("Tapez DÉMARRER pour piloter !") }

    val obstacles = remember { mutableStateListOf<RaceObstacle>() }
    val collectibles = remember { mutableStateListOf<RoadCollectible>() }
    var activeChallenge by remember { mutableStateOf<ScienceChallenge?>(null) }
    var challengeFeedback by remember { mutableStateOf<String?>(null) }

    var scrollOffset by remember { mutableFloatStateOf(0f) }
    var nextSpawnTime by remember { mutableLongStateOf(0L) }
    var nextChallengeDistance by remember { mutableFloatStateOf(400f) }

    // Smooth lane transition animation
    LaunchedEffect(targetLane) {
        laneAnim.animateTo(targetLane.toFloat(), animationSpec = tween(120))
    }

    // Main Game Loop (60 FPS tick)
    LaunchedEffect(isRacing, isGameOver, activeChallenge != null) {
        if (isRacing && !isGameOver && activeChallenge == null) {
            var lastNano = 0L
            while (isRacing && !isGameOver && activeChallenge == null) {
                withFrameNanos { now ->
                    if (lastNano != 0L) {
                        val dt = (now - lastNano) / 1_000_000_000f // in seconds

                        // Speed physics
                        val baseTargetSpeed = if (isNitroActive) 235f else 145f
                        speedKmh += (baseTargetSpeed - speedKmh) * (dt * 2f)

                        // Nitro consumption
                        if (isNitroActive) {
                            nitroEnergy = (nitroEnergy - dt * 25f).coerceAtLeast(0f)
                            if (nitroEnergy <= 0f) isNitroActive = false
                        } else {
                            nitroEnergy = (nitroEnergy + dt * 4f).coerceAtMost(100f)
                        }

                        // Advance distance & road scroll
                        val distanceIncrement = (speedKmh / 3.6f) * dt
                        distanceTraveled += distanceIncrement
                        scrollOffset += dt * (speedKmh / 35f)
                        score += (distanceIncrement * 0.8f).toInt()

                        // Check Finish Line
                        if (distanceTraveled >= targetDistance) {
                            isGameOver = true
                            isRacing = false
                            statusMessage = "🏁 LIGNE D'ARRIVÉE FRANCHIE ! VICTOIRE !"
                            coroutineScope.launch {
                                repository.recordRaceResult(
                                    score = score,
                                    xpEarned = score / 5 + 100,
                                    subject = subject.name,
                                    mecaBonus = mecaCount * 25,
                                    progBonus = progCount * 25,
                                    mathBonus = mathCount * 25,
                                    details = "Victoire 2.5km | Vitesse max: ${speedKmh.toInt()} km/h"
                                )
                            }
                        }

                        // Move existing collectibles and obstacles towards player
                        val speedFactor = dt * (speedKmh / 140f) * 0.85f

                        // Update obstacles
                        val obsIter = obstacles.iterator()
                        while (obsIter.hasNext()) {
                            val obs = obsIter.next()
                            obs.yPos += speedFactor
                            // Collision check with player (player is at laneAnim.value, y ≈ 0.82)
                            if (obs.yPos in 0.78f..0.88f && kotlin.math.abs(laneAnim.value - obs.lane) < 0.45f) {
                                // Hit obstacle!
                                speedKmh = (speedKmh * 0.6f).coerceAtLeast(60f)
                                statusMessage = "⚠️ Obstacle touché ! Décélération !"
                                obsIter.remove()
                            } else if (obs.yPos > 1.05f) {
                                obsIter.remove()
                            }
                        }

                        // Update collectibles
                        val colIter = collectibles.iterator()
                        while (colIter.hasNext()) {
                            val col = colIter.next()
                            col.yPos += speedFactor
                            // Collection check
                            if (col.yPos in 0.76f..0.90f && kotlin.math.abs(laneAnim.value - col.lane) < 0.5f) {
                                score += 150
                                nitroEnergy = (nitroEnergy + 20f).coerceAtMost(100f)
                                when (col.type) {
                                    CollectibleType.GEAR -> mecaCount++
                                    CollectibleType.BIT -> progCount++
                                    CollectibleType.PI -> mathCount++
                                    CollectibleType.NITRO -> speedKmh = (speedKmh + 35f).coerceAtMost(250f)
                                }
                                statusMessage = "Bonus ramassé : ${col.type.label} !"
                                colIter.remove()
                            } else if (col.yPos > 1.05f) {
                                colIter.remove()
                            }
                        }

                        // Spawner
                        if (now - nextSpawnTime > 1_300_000_000L) { // every ~1.3 seconds
                            nextSpawnTime = now
                            val lane = Random.nextInt(3)
                            if (Random.nextBoolean()) {
                                // Spawn collectible
                                val colType = CollectibleType.entries.random()
                                collectibles.add(RoadCollectible(System.currentTimeMillis(), lane, 0.05f, colType))
                            } else {
                                // Spawn obstacle in a different lane
                                val obsType = RaceObstacleType.entries.random()
                                obstacles.add(RaceObstacle(System.currentTimeMillis(), lane, 0.05f, obsType))
                            }
                        }

                        // Trigger Science Checkpoint challenge every 400m
                        if (distanceTraveled >= nextChallengeDistance) {
                            nextChallengeDistance += 450f
                            activeChallenge = QuestionBank.getRandomChallenge(subject)
                        }
                    }
                    lastNano = now
                }
            }
        }
    }

    currentLane = laneAnim.value

    val gear = when {
        speedKmh < 60 -> 2
        speedKmh < 100 -> 3
        speedKmh < 150 -> 4
        speedKmh < 200 -> 5
        else -> 6
    }
    val rpm = 3000f + (speedKmh % 50f) * 80f

    val gameState = RaceGameState(
        isRacing = isRacing,
        isGameOver = isGameOver,
        currentLane = currentLane,
        targetLane = targetLane,
        speedKmh = speedKmh,
        distanceTraveled = distanceTraveled,
        targetDistance = targetDistance,
        nitroEnergy = nitroEnergy,
        isNitroActive = isNitroActive,
        score = score,
        gear = gear,
        rpm = rpm,
        obstacles = obstacles,
        collectibles = collectibles,
        activeChallenge = activeChallenge,
        subject = subject,
        statusMessage = statusMessage
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B19))
    ) {
        // 1. Dynamic 3D Perspective Road Canvas
        RaceCanvas(
            state = gameState,
            scrollOffset = scrollOffset,
            textMeasurer = textMeasurer,
            modifier = Modifier.fillMaxSize(),
            onTapLane = { lane ->
                if (isRacing && activeChallenge == null) {
                    targetLane = lane
                }
            }
        )

        // 2. Race Top HUD Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Top Bar: Back button, subject badge, Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(Color(0x80000000), CircleShape)
                        .testTag("race_back_button")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.White
                    )
                }

                // Subject Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = subject.color.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, subject.color)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(subject.icon, fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            subject.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Score Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x990F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "$score PTS",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Gauge Telemetry Cluster: Speedometer, Gear, Distance
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xCC0B1329),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3338BDF8)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speedometer Display
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            "${speedKmh.toInt()}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = if (speedKmh > 200f) Color(0xFF00D2FF) else Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                        Text("KM/H • VITESSE", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    }

                    // Gear Box
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            Text(
                                "R$gear",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFF8A00)
                            )
                        }
                        Text("${rpm.toInt()} RPM", fontSize = 9.sp, color = Color(0xFF94A3B8))
                    }

                    // Distance progress
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "${(targetDistance - distanceTraveled).toInt().coerceAtLeast(0)} m",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            fontFamily = FontFamily.Monospace
                        )
                        Text("DISTANCE RESTANTE", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Nitro Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Bolt, contentDescription = "Nitro", tint = Color(0xFF00D2FF), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(6.dp))
                LinearProgressIndicator(
                    progress = { nitroEnergy / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF00D2FF),
                    trackColor = Color(0xFF1E293B)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "${nitroEnergy.toInt()}%",
                    color = Color(0xFF00D2FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        // 3. In-Game Interactive Science Checkpoint Dialog
        AnimatedVisibility(
            visible = activeChallenge != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp)
        ) {
            activeChallenge?.let { challenge ->
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xF20F172A)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, challenge.subject.color),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("science_challenge_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(challenge.subject.icon, fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "CHECKPOINT SCIENTIFIQUE",
                                color = challenge.subject.color,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            challenge.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(8.dp))

                        // Formula or Code snippet if present
                        if (challenge.formulaSnippet != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Text(
                                    challenge.formulaSnippet,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    color = Color(0xFFFFD54F),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else if (challenge.codeSnippet != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF111827),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Text(
                                    challenge.codeSnippet,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = Color(0xFF38BDF8),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            challenge.question,
                            fontSize = 13.sp,
                            color = Color(0xFFE2E8F0),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        // 4 Answer Options
                        challenge.options.forEachIndexed { index, optionText ->
                            Button(
                                onClick = {
                                    val isCorrect = (index == challenge.correctIndex)
                                    if (isCorrect) {
                                        score += 500
                                        nitroEnergy = 100f
                                        isNitroActive = true
                                        speedKmh = (speedKmh + 65f).coerceAtMost(260f)
                                        comboStreak++
                                        when (challenge.subject) {
                                            Subject.MECANIQUE -> mecaCount++
                                            Subject.PROGRAMMATION -> progCount++
                                            Subject.MATHS -> mathCount++
                                            Subject.MIXTE -> {}
                                        }
                                        statusMessage = "🚀 ${challenge.gameBonusEffect}"
                                    } else {
                                        speedKmh = (speedKmh * 0.7f).coerceAtLeast(70f)
                                        comboStreak = 0
                                        statusMessage = "Erreur ! Vitesse réduite."
                                    }
                                    activeChallenge = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("option_$index"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1E293B)
                                )
                            ) {
                                Text(
                                    optionText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Bottom Cockpit Controls (Steer Left, Steer Right, Nitro Button)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status bar message
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xB3000000),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Text(
                    statusMessage,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (!isRacing && !isGameOver) {
                Button(
                    onClick = {
                        isRacing = true
                        statusMessage = "Accélération en cours ! Esquivez et ramassez !"
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(56.dp)
                        .testTag("start_race_button"),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("DÉMARRER LA COURSE", fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            } else if (isGameOver) {
                // Game Over / Victory Modal Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xF00F172A)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF10B981)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏁 COURSE TERMINÉE", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD54F))
                        Spacer(Modifier.height(8.dp))
                        Text("Score Final: $score PTS", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Sciences validées : ⚙️ $mecaCount Méca | 💻 $progCount Prog | 📐 $mathCount Maths",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FilledTonalButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                                Text("Menu Principal")
                            }
                            Button(
                                onClick = {
                                    distanceTraveled = 0f
                                    score = 0
                                    obstacles.clear()
                                    collectibles.clear()
                                    isGameOver = false
                                    isRacing = true
                                    statusMessage = "C'est reparti !"
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Icon(Icons.Default.Replay, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Rejouer")
                            }
                        }
                    }
                }
            } else {
                // In-race Steering & Nitro Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Steer Left Button
                    Button(
                        onClick = {
                            if (targetLane > 0) targetLane--
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("steer_left_button"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC1E293B)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Gauche", tint = Color.White, modifier = Modifier.size(36.dp))
                    }

                    // Super Nitro Button
                    Button(
                        onClick = {
                            if (nitroEnergy >= 25f) {
                                isNitroActive = true
                                statusMessage = "⚡ SUPER NITRO ACTIVÉ !"
                            } else {
                                statusMessage = "Nitro insuffisant (besoin de 25%) !"
                            }
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                            .padding(horizontal = 14.dp)
                            .testTag("nitro_button"),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isNitroActive) Color(0xFF00D2FF) else Color(0xFF2563EB)
                        )
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (isNitroActive) "NITRO EN COURS !" else "BOOST NITRO",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }

                    // Steer Right Button
                    Button(
                        onClick = {
                            if (targetLane < 2) targetLane++
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("steer_right_button"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC1E293B)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Droite", tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                }
            }
        }
    }
}
