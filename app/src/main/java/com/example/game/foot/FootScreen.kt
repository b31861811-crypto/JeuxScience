package com.example.game.foot

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun FootScreen(
    subject: Subject,
    repository: GameRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()

    var isFreeKick by remember { mutableStateOf(false) }
    val distanceMeters = if (isFreeKick) 22f else 11f

    var aimAngleDeg by remember { mutableFloatStateOf(18f) }
    var aimDirectionDeg by remember { mutableFloatStateOf(0f) }
    var kickPowerMs by remember { mutableFloatStateOf(24f) }
    var spinRpm by remember { mutableFloatStateOf(0f) }

    var ballX by remember { mutableFloatStateOf(0f) }
    var ballY by remember { mutableFloatStateOf(0f) }
    var ballZ by remember { mutableFloatStateOf(distanceMeters) }
    var isBallInFlight by remember { mutableStateOf(false) }

    var keeperX by remember { mutableFloatStateOf(0f) }
    var keeperY by remember { mutableFloatStateOf(1.0f) }
    var keeperDecision by remember { mutableStateOf(KeeperDecision.CENTER) }

    var scoreGoals by remember { mutableIntStateOf(0) }
    var totalShots by remember { mutableIntStateOf(0) }
    var outcome by remember { mutableStateOf(ShotOutcome.NONE) }
    var outcomeMessage by remember { mutableStateOf("Prêt pour la frappe !") }
    var hasPredictiveVision by remember { mutableStateOf(false) }

    var activeChallenge by remember { mutableStateOf<ScienceChallenge?>(null) }
    var streak by remember { mutableIntStateOf(0) }

    // Goalkeeper Algorithm Script snippet
    val keeperAlgorithmSnippet = remember(totalShots) {
        val ruleChoice = totalShots % 3
        when (ruleChoice) {
            0 -> """
                // ALGORITHME DU GARDIEN #1:
                if (ball.spin < -8) {
                    diveLeft()
                } else if (ball.spin > 8) {
                    diveRight()
                } else if (ball.angle > 28) {
                    jumpHighCenter()
                } else {
                    stayCenter()
                }
            """.trimIndent()
            1 -> """
                // ALGORITHME DU GARDIEN #2:
                if (ball.aimDir < -6) {
                    diveLeft()
                } else if (ball.aimDir > 6) {
                    diveRight()
                } else {
                    anticipateCenter()
                }
            """.trimIndent()
            else -> """
                // ALGORITHME DU GARDIEN #3:
                val isHigh = (ball.power > 26 && ball.angle > 20)
                if (isHigh && ball.aimDir > 0) {
                    diveRightHigh()
                } else if (isHigh) {
                    diveLeftHigh()
                } else {
                    diveLowRandom()
                }
            """.trimIndent()
        }
    }

    // Function to calculate Goalkeeper dive according to the script
    fun decideKeeperDive(): KeeperDecision {
        val ruleChoice = totalShots % 3
        return when (ruleChoice) {
            0 -> {
                when {
                    spinRpm < -8 -> KeeperDecision.DIVE_LEFT_LOW
                    spinRpm > 8 -> KeeperDecision.DIVE_RIGHT_LOW
                    aimAngleDeg > 28 -> KeeperDecision.JUMP_CENTER_HIGH
                    else -> KeeperDecision.CENTER
                }
            }
            1 -> {
                when {
                    aimDirectionDeg < -6 -> KeeperDecision.DIVE_LEFT_LOW
                    aimDirectionDeg > 6 -> KeeperDecision.DIVE_RIGHT_LOW
                    else -> KeeperDecision.CENTER
                }
            }
            else -> {
                val isHigh = (kickPowerMs > 26 && aimAngleDeg > 20)
                if (isHigh && aimDirectionDeg > 0) {
                    KeeperDecision.DIVE_RIGHT_HIGH
                } else if (isHigh) {
                    KeeperDecision.DIVE_LEFT_HIGH
                } else {
                    if (Random.nextBoolean()) KeeperDecision.DIVE_LEFT_LOW else KeeperDecision.DIVE_RIGHT_LOW
                }
            }
        }
    }

    // Ball Flight Physics Coroutine Loop
    fun launchShot() {
        if (isBallInFlight) return

        totalShots++
        isBallInFlight = true
        outcome = ShotOutcome.NONE
        outcomeMessage = "Frappe en cours..."

        // Determine goalkeeper decision using the algorithm
        val keeperDec = decideKeeperDive()
        keeperDecision = keeperDec

        coroutineScope.launch {
            val thetaRad = Math.toRadians(aimAngleDeg.toDouble()).toFloat()
            val phiRad = Math.toRadians(aimDirectionDeg.toDouble()).toFloat()
            val v0 = kickPowerMs
            val g = 9.81f

            val vz0 = v0 * cos(thetaRad) * cos(phiRad)
            val vx0 = v0 * cos(thetaRad) * sin(phiRad)
            val vy0 = v0 * sin(thetaRad)

            var t = 0f
            val dt = 0.016f // 60 FPS step
            var currentZ = distanceMeters

            // Animate ball trajectory step by step
            while (currentZ > 0f) {
                t += dt
                currentZ = (distanceMeters - vz0 * t).coerceAtLeast(0f)

                // Magnus effect lateral deflection: x(t) = vx0*t + 0.5 * (spin * 0.08) * t²
                val magnusDeflection = (spinRpm * 0.035f) * (t * t)
                val currentX = vx0 * t + magnusDeflection

                // Vertical parabolic motion: y(t) = vy0*t - 0.5*g*t²
                val currentY = (vy0 * t - 0.5f * g * (t * t)).coerceAtLeast(0f)

                ballX = currentX
                ballY = currentY
                ballZ = currentZ

                // Animate goalkeeper moving toward target
                val diveProgress = (1f - (currentZ / distanceMeters)).coerceIn(0f, 1f)
                keeperX = keeperDec.targetX * diveProgress
                keeperY = 1.0f + (keeperDec.targetY - 1.0f) * diveProgress

                // Check collision with wall (if free kick at z = 9.15m from ball, i.e., currentZ = distanceMeters - 9.15m)
                if (isFreeKick && currentZ <= (distanceMeters - 9.15f) && currentZ >= (distanceMeters - 9.5f)) {
                    if (kotlin.math.abs(currentX) < 1.4f && currentY < 1.85f) {
                        // Hit defensive wall!
                        outcome = ShotOutcome.HIT_WALL
                        outcomeMessage = "🧱 Contré par le mur à 9.15m ! (Hauteur < 1.85m)"
                        break
                    }
                }

                delay(16)
            }

            // Once ball reaches goal line (z = 0)
            if (outcome == ShotOutcome.NONE) {
                // Goal dimensions: width [-3.66..3.66], height [0..2.44]
                val goalHalfWidth = 3.66f
                val goalHeight = 2.44f

                val isWithinWidth = ballX in -goalHalfWidth..goalHalfWidth
                val isWithinHeight = ballY in 0.05f..goalHeight

                // Check distance to Goalkeeper
                val distToKeeper = kotlin.math.hypot(ballX - keeperX, ballY - keeperY)

                outcome = when {
                    !isWithinWidth -> {
                        outcomeMessage = "❌ Tir non cadré (hors cadre) !"
                        streak = 0
                        ShotOutcome.MISSED_WIDE
                    }
                    ballY > goalHeight + 0.3f -> {
                        outcomeMessage = "❌ Trop haut ! Au-dessus de la barre !"
                        streak = 0
                        ShotOutcome.MISSED_OVER
                    }
                    kotlin.math.abs(ballY - goalHeight) < 0.2f && isWithinWidth -> {
                        outcomeMessage = "💥 Transversale rentrante ou sortante !"
                        ShotOutcome.CROSSBAR_POST
                    }
                    distToKeeper < 0.95f -> {
                        outcomeMessage = "🧤 Arrêt splendide du gardien !"
                        streak = 0
                        ShotOutcome.KEEPER_SAVED
                    }
                    isWithinWidth && isWithinHeight -> {
                        scoreGoals++
                        streak++
                        val isLucarne = (kotlin.math.abs(ballX) > 2.7f && ballY > 1.9f)
                        outcomeMessage = if (isLucarne) "🎯 BUUUT EN PLEINE LUCARNE ! MAGNIFIQUE !" else "⚽ BUUUUUT ! FILETS TREMBLANTS !"
                        // Record XP
                        repository.recordFootResult(
                            goals = 1,
                            shots = 1,
                            xpEarned = if (isLucarne) 200 else 120,
                            subject = subject.name,
                            mecaBonus = 40,
                            progBonus = 40,
                            mathBonus = 40,
                            details = if (isLucarne) "But en pleine lucarne !" else "But marqué !"
                        )
                        ShotOutcome.GOAL
                    }
                    else -> ShotOutcome.MISSED_WIDE
                }
            }

            hasPredictiveVision = false
            isBallInFlight = false
        }
    }

    val gameState = FootGameState(
        isFreeKick = isFreeKick,
        distanceMeters = distanceMeters,
        aimAngleDeg = aimAngleDeg,
        aimDirectionDeg = aimDirectionDeg,
        kickPowerMs = kickPowerMs,
        spinRpm = spinRpm,
        ballX = ballX,
        ballY = ballY,
        ballZ = ballZ,
        isBallInFlight = isBallInFlight,
        keeperX = keeperX,
        keeperY = keeperY,
        keeperDecision = keeperDecision,
        keeperAlgorithmSnippet = keeperAlgorithmSnippet,
        scoreGoals = scoreGoals,
        totalShots = totalShots,
        outcome = outcome,
        outcomeMessage = outcomeMessage,
        subject = subject,
        hasPredictiveVision = hasPredictiveVision,
        streak = streak
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B19))
    ) {
        // 1. Stadium Canvas
        FootCanvas(
            state = gameState,
            textMeasurer = textMeasurer,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top Header HUD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(Color(0x80000000), CircleShape)
                        .testTag("foot_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }

                // Mode Toggle: Penalty (11m) vs Coup Franc (22m avec mur)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xCC0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        FilledTonalButton(
                            onClick = {
                                isFreeKick = false
                                ballZ = 11f
                                ballX = 0f
                                ballY = 0f
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (!isFreeKick) Color(0xFF2563EB) else Color.Transparent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Penalty (11m)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        FilledTonalButton(
                            onClick = {
                                isFreeKick = true
                                ballZ = 22f
                                ballX = 0f
                                ballY = 0f
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (isFreeKick) Color(0xFF2563EB) else Color.Transparent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Coup Franc (22m)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Score Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x990F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "$scoreGoals / $totalShots",
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("BUTS", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Big Stadium Screen with Goalkeeper Script (Programmation revision in real-time)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xE6050C1A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF00D2FF), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("SCRIPT IA DU GARDIEN (DÉCHIFFREZ LE CODE)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF00D2FF))
                        }
                        if (hasPredictiveVision) {
                            Text("VISION ACTIVÉE ✨", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                        }
                    }
                    Text(
                        keeperAlgorithmSnippet,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // 3. Pre-Shot Science Challenge Modal (if user wants predictive vision & bonus power)
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
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xF20F172A)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, challenge.subject.color),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(challenge.subject.icon, fontSize = 22.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "DÉFI COUP FRANC SCIENTIFIQUE",
                                color = challenge.subject.color,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(Modifier.height(10.dp))
                        Text(
                            challenge.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        if (challenge.formulaSnippet != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Text(
                                    challenge.formulaSnippet,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = Color(0xFFFFD54F),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Text(
                            challenge.question,
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Spacer(Modifier.height(10.dp))

                        challenge.options.forEachIndexed { index, opt ->
                            Button(
                                onClick = {
                                    val isCorrect = (index == challenge.correctIndex)
                                    if (isCorrect) {
                                        hasPredictiveVision = true
                                        outcomeMessage = "✨ Bonne réponse ! Vision prédictive du gardien débloquée !"
                                    } else {
                                        outcomeMessage = "Mauvaise réponse ! Tir standard sans aide."
                                    }
                                    activeChallenge = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                            ) {
                                Text(opt, fontSize = 12.sp, color = Color.White, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }

        // 4. Bottom Controls: Sliders for Physics & Shoot Button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Outcome Message Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (outcome) {
                    ShotOutcome.GOAL -> Color(0xFF166534)
                    ShotOutcome.KEEPER_SAVED, ShotOutcome.MISSED_WIDE, ShotOutcome.MISSED_OVER, ShotOutcome.HIT_WALL -> Color(0xFF991B1B)
                    else -> Color(0xB3000000)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 6.dp)
            ) {
                Text(
                    outcomeMessage,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Controls Container
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xF20F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3338BDF8)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    // Row 1: Vertical Angle θ and Kick Power v0
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Angle slider
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Angle (θ)", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                Text("${aimAngleDeg.toInt()}°", fontSize = 11.sp, color = Color(0xFFFF8A00), fontWeight = FontWeight.Black)
                            }
                            Slider(
                                value = aimAngleDeg,
                                onValueChange = { aimAngleDeg = it },
                                valueRange = 10f..42f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFFF8A00), activeTrackColor = Color(0xFFFF8A00))
                            )
                        }

                        // Power slider
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Vitesse (v0)", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                Text("${kickPowerMs.toInt()} m/s (${(kickPowerMs * 3.6f).toInt()} km/h)", fontSize = 11.sp, color = Color(0xFF00D2FF), fontWeight = FontWeight.Black)
                            }
                            Slider(
                                value = kickPowerMs,
                                onValueChange = { kickPowerMs = it },
                                valueRange = 16f..36f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF00D2FF), activeTrackColor = Color(0xFF00D2FF))
                            )
                        }
                    }

                    // Row 2: Direction & Magnus Spin ω
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Aim Direction
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Visée Horiz.", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                Text("${aimDirectionDeg.toInt()}°", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = aimDirectionDeg,
                                onValueChange = { aimDirectionDeg = it },
                                valueRange = -18f..18f,
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF64748B))
                            )
                        }

                        // Magnus Spin
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Effet Magnus (Spin)", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                Text(
                                    if (spinRpm < -3) "Brossé G" else if (spinRpm > 3) "Brossé D" else "Neutre",
                                    fontSize = 11.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = spinRpm,
                                onValueChange = { spinRpm = it },
                                valueRange = -25f..25f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF10B981), activeTrackColor = Color(0xFF10B981))
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Action Buttons Row: [DÉFI SCIENCE] + [TIRER !]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                activeChallenge = QuestionBank.getRandomChallenge(subject)
                            },
                            modifier = Modifier
                                .weight(0.45f)
                                .height(50.dp)
                                .testTag("science_boost_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Défi Science", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (!isBallInFlight) {
                                    launchShot()
                                }
                            },
                            enabled = !isBallInFlight,
                            modifier = Modifier
                                .weight(0.55f)
                                .height(50.dp)
                                .testTag("shoot_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Icon(Icons.Default.SportsSoccer, contentDescription = null, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("TIRER !", fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}
