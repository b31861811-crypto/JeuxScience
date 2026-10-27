package com.example.game.race

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.sin

@Composable
fun RaceCanvas(
    state: RaceGameState,
    scrollOffset: Float,
    textMeasurer: TextMeasurer,
    modifier: Modifier = Modifier,
    onTapLane: (Int) -> Unit = {}
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val lane = when {
                        offset.x < size.width * 0.33f -> 0
                        offset.x < size.width * 0.66f -> 1
                        else -> 2
                    }
                    onTapLane(lane)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val lane = when {
                        change.position.x < size.width * 0.33f -> 0
                        change.position.x < size.width * 0.66f -> 1
                        else -> 2
                    }
                    onTapLane(lane)
                }
            }
    ) {
        val width = size.width
        val height = size.height

        // 1. Draw Sky / Horizon background
        val skyBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF070B19), Color(0xFF0F1A30), Color(0xFF1E293B)),
            startY = 0f,
            endY = height * 0.4f
        )
        drawRect(brush = skyBrush, size = Size(width, height * 0.4f))

        // Horizon line at 35% height
        val horizonY = height * 0.35f

        // Draw distant city / stadium lights on horizon
        for (i in 0 until 12) {
            val hx = i * (width / 12f) + (width / 24f)
            val glowColor = if (i % 2 == 0) Color(0xFF00D2FF).copy(alpha = 0.3f) else Color(0xFFFF8A00).copy(alpha = 0.3f)
            drawCircle(color = glowColor, radius = 8f, center = Offset(hx, horizonY - 15f))
        }

        // 2. Draw Road with Perspective
        val roadTopWidth = width * 0.35f
        val roadBottomWidth = width * 0.92f

        val roadTopLeft = Offset((width - roadTopWidth) / 2f, horizonY)
        val roadTopRight = Offset((width + roadTopWidth) / 2f, horizonY)
        val roadBottomLeft = Offset((width - roadBottomWidth) / 2f, height)
        val roadBottomRight = Offset((width + roadBottomWidth) / 2f, height)

        // Road grass/sides
        drawRect(
            color = Color(0xFF0D1B2A),
            topLeft = Offset(0f, horizonY),
            size = Size(width, height - horizonY)
        )

        // Red and white curbs (vibreurs de circuit)
        val curbMargin = 16f
        val leftCurbPath = Path().apply {
            moveTo(roadTopLeft.x - curbMargin, roadTopLeft.y)
            lineTo(roadTopLeft.x, roadTopLeft.y)
            lineTo(roadBottomLeft.x, roadBottomLeft.y)
            lineTo(roadBottomLeft.x - curbMargin * 2.5f, roadBottomLeft.y)
            close()
        }
        val rightCurbPath = Path().apply {
            moveTo(roadTopRight.x, roadTopRight.y)
            lineTo(roadTopRight.x + curbMargin, roadTopRight.y)
            lineTo(roadBottomRight.x + curbMargin * 2.5f, roadBottomRight.y)
            lineTo(roadBottomRight.x, roadBottomRight.y)
            close()
        }
        drawPath(leftCurbPath, Color(0xFFDC2626))
        drawPath(rightCurbPath, Color(0xFFDC2626))

        // Main asphalt surface
        val roadPath = Path().apply {
            moveTo(roadTopLeft.x, roadTopLeft.y)
            lineTo(roadTopRight.x, roadTopRight.y)
            lineTo(roadBottomRight.x, roadBottomRight.y)
            lineTo(roadBottomLeft.x, roadBottomLeft.y)
            close()
        }
        drawPath(roadPath, Color(0xFF1E2430))

        // 3. Lane dividers (perspective dashed lines)
        val laneCount = 3
        val stripeSegments = 12
        for (l in 1 until laneCount) {
            val topX = roadTopLeft.x + (roadTopWidth / laneCount) * l
            val bottomX = roadBottomLeft.x + (roadBottomWidth / laneCount) * l

            for (s in 0 until stripeSegments) {
                val prog1 = (s.toFloat() + (scrollOffset % 1f)) / stripeSegments
                val prog2 = prog1 + 0.05f
                if (prog1 in 0f..1f && prog2 in 0f..1f) {
                    val y1 = horizonY + (height - horizonY) * (prog1 * prog1)
                    val y2 = horizonY + (height - horizonY) * (prog2 * prog2)
                    val x1 = topX + (bottomX - topX) * prog1
                    val x2 = topX + (bottomX - topX) * prog2
                    val strokeW = 2f + 6f * prog1

                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = strokeW
                    )
                }
            }
        }

        // 4. Draw Collectibles & Obstacles
        // Collectibles (Gears, Bits, Pi, Nitro)
        for (c in state.collectibles) {
            val t = c.yPos
            if (t in 0.05f..0.98f) {
                val y = horizonY + (height - horizonY) * (t * t)
                val currentRoadW = roadTopWidth + (roadBottomWidth - roadTopWidth) * t
                val currentRoadL = (width - currentRoadW) / 2f
                val laneW = currentRoadW / 3f
                val x = currentRoadL + laneW * (c.lane + 0.5f)

                val radius = 10f + 22f * t
                drawCircle(
                    color = c.type.color.copy(alpha = 0.25f),
                    radius = radius * 1.5f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = c.type.color,
                    radius = radius,
                    center = Offset(x, y)
                )
                // Draw text symbol
                val textLayout = textMeasurer.measure(
                    text = c.type.symbol,
                    style = TextStyle(
                        fontSize = (10 + 12 * t).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(x - textLayout.size.width / 2f, y - textLayout.size.height / 2f)
                )
            }
        }

        // Obstacles (Oil, Glitch bug, Gravel)
        for (obs in state.obstacles) {
            val t = obs.yPos
            if (t in 0.05f..0.98f) {
                val y = horizonY + (height - horizonY) * (t * t)
                val currentRoadW = roadTopWidth + (roadBottomWidth - roadTopWidth) * t
                val currentRoadL = (width - currentRoadW) / 2f
                val laneW = currentRoadW / 3f
                val x = currentRoadL + laneW * (obs.lane + 0.5f)
                val w = (20f + 50f * t)

                when (obs.type) {
                    RaceObstacleType.OIL_SLICK -> {
                        // Glossy dark oil puddle
                        drawOval(
                            color = Color(0xFF0F0F14),
                            topLeft = Offset(x - w / 2f, y - w * 0.25f),
                            size = Size(w, w * 0.5f)
                        )
                        drawOval(
                            color = Color(0xFF6366F1).copy(alpha = 0.4f),
                            topLeft = Offset(x - w * 0.3f, y - w * 0.15f),
                            size = Size(w * 0.6f, w * 0.3f)
                        )
                    }
                    RaceObstacleType.CODE_BUG -> {
                        // Digital red glitch barrier
                        drawRoundRect(
                            color = Color(0xFFEF4444),
                            topLeft = Offset(x - w * 0.4f, y - w * 0.3f),
                            size = Size(w * 0.8f, w * 0.4f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                        )
                        val bugText = textMeasurer.measure(
                            text = "BUG",
                            style = TextStyle(fontSize = (9 + 8 * t).sp, fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        drawText(bugText, topLeft = Offset(x - bugText.size.width / 2f, y - bugText.size.height / 2f))
                    }
                    RaceObstacleType.GRAVEL_TRAP -> {
                        // Gravel patch
                        drawRoundRect(
                            color = Color(0xFFD97706),
                            topLeft = Offset(x - w * 0.45f, y - w * 0.2f),
                            size = Size(w * 0.9f, w * 0.4f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )
                    }
                }
            }
        }

        // 5. Draw Player's Racing Car
        val carY = height * 0.82f
        val carRoadW = roadTopWidth + (roadBottomWidth - roadTopWidth) * 0.82f
        val carRoadL = (width - carRoadW) / 2f
        val carLaneW = carRoadW / 3f

        // Smooth lane interpolation
        val carX = carRoadL + carLaneW * (state.currentLane + 0.5f)
        drawPlayerCar(
            scope = this,
            x = carX,
            y = carY,
            color = state.carColor,
            isNitro = state.isNitroActive,
            speedRatio = state.speedKmh / state.maxSpeedKmh
        )

        // 6. Draw Speed Lines if going fast
        if (state.speedKmh > 180f) {
            val alpha = ((state.speedKmh - 180f) / 80f).coerceIn(0.1f, 0.6f)
            for (i in 0 until 8) {
                val ly = horizonY + (height - horizonY) * ((i * 0.13f + scrollOffset * 0.5f) % 1f)
                drawLine(
                    color = Color(0xFF00D2FF).copy(alpha = alpha),
                    start = Offset(width * 0.05f, ly),
                    end = Offset(width * 0.12f, ly + 25f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color(0xFF00D2FF).copy(alpha = alpha),
                    start = Offset(width * 0.95f, ly),
                    end = Offset(width * 0.88f, ly + 25f),
                    strokeWidth = 3f
                )
            }
        }
    }
}

private fun drawPlayerCar(
    scope: DrawScope,
    x: Float,
    y: Float,
    color: Color,
    isNitro: Boolean,
    speedRatio: Float
) {
    scope.apply {
        val carW = 68f
        val carH = 110f

        // Nitro flame effect behind car
        if (isNitro) {
            val flameH = 35f + 15f * sin(System.currentTimeMillis() * 0.03f).toFloat()
            val flameBrush = Brush.verticalGradient(
                colors = listOf(Color(0xFF00D2FF), Color(0xFF3B82F6), Color.Transparent),
                startY = y + carH * 0.45f,
                endY = y + carH * 0.45f + flameH
            )
            drawOval(
                brush = flameBrush,
                topLeft = Offset(x - 14f, y + carH * 0.35f),
                size = Size(28f, flameH)
            )
        }

        // Tire skid marks on ground
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            topLeft = Offset(x - carW * 0.45f, y + carH * 0.4f),
            size = Size(14f, 25f)
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            topLeft = Offset(x + carW * 0.45f - 14f, y + carH * 0.4f),
            size = Size(14f, 25f)
        )

        // Tires (4 wide racing slick tires)
        val tireColor = Color(0xFF18181B)
        // Front left & right
        drawRoundRect(
            color = tireColor,
            topLeft = Offset(x - carW * 0.55f, y - carH * 0.35f),
            size = Size(14f, 28f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = tireColor,
            topLeft = Offset(x + carW * 0.55f - 14f, y - carH * 0.35f),
            size = Size(14f, 28f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        // Rear left & right
        drawRoundRect(
            color = tireColor,
            topLeft = Offset(x - carW * 0.58f, y + carH * 0.15f),
            size = Size(16f, 32f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = tireColor,
            topLeft = Offset(x + carW * 0.58f - 16f, y + carH * 0.15f),
            size = Size(16f, 32f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )

        // Aerodynamic Chassis body
        val chassisPath = Path().apply {
            moveTo(x, y - carH * 0.5f) // Nose
            lineTo(x + carW * 0.28f, y - carH * 0.2f)
            lineTo(x + carW * 0.4f, y + carH * 0.35f)
            lineTo(x - carW * 0.4f, y + carH * 0.35f)
            lineTo(x - carW * 0.28f, y - carH * 0.2f)
            close()
        }
        drawPath(chassisPath, color)

        // Cockpit & Helmet
        drawOval(
            color = Color(0xFF1E293B),
            topLeft = Offset(x - 14f, y - carH * 0.1f),
            size = Size(28f, 38f)
        )
        drawCircle(
            color = Color(0xFFFFD54F), // Yellow racing helmet
            radius = 7f,
            center = Offset(x, y + 2f)
        )

        // Front Wing / Spoiler
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(x - carW * 0.48f, y - carH * 0.52f),
            size = Size(carW * 0.96f, 10f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
        )

        // Rear Wing / Spoiler
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(x - carW * 0.52f, y + carH * 0.38f),
            size = Size(carW * 1.04f, 12f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )

        // Tech racing livery stripe down center
        drawLine(
            color = Color.White.copy(alpha = 0.85f),
            start = Offset(x, y - carH * 0.48f),
            end = Offset(x, y + carH * 0.35f),
            strokeWidth = 3f
        )
    }
}
