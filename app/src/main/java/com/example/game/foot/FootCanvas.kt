package com.example.game.foot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FootCanvas(
    state: FootGameState,
    textMeasurer: TextMeasurer,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Stadium Grass & Sky background
        val horizonY = height * 0.28f

        // Stadium background / crowd stands
        val stadiumBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF070B19), Color(0xFF0F172A), Color(0xFF1E293B)),
            startY = 0f,
            endY = horizonY
        )
        drawRect(brush = stadiumBrush, size = Size(width, horizonY))

        // Stadium floodlights
        for (i in 0 until 10) {
            val fx = width * (0.05f + i * 0.1f)
            drawCircle(
                color = Color(0xFFF8FAFC).copy(alpha = 0.4f),
                radius = 6f,
                center = Offset(fx, horizonY * 0.4f)
            )
        }

        // Green Grass Pitch with alternating stripes
        val grassStripeCount = 12
        for (i in 0 until grassStripeCount) {
            val y1 = horizonY + (height - horizonY) * (i.toFloat() / grassStripeCount)
            val y2 = horizonY + (height - horizonY) * ((i + 1).toFloat() / grassStripeCount)
            val color = if (i % 2 == 0) Color(0xFF15803D) else Color(0xFF166534)
            drawRect(
                color = color,
                topLeft = Offset(0f, y1),
                size = Size(width, y2 - y1)
            )
        }

        // Penalty box lines
        val boxLeft = width * 0.12f
        val boxRight = width * 0.88f
        val boxTop = horizonY + (height - horizonY) * 0.15f
        drawRect(
            color = Color.White.copy(alpha = 0.4f),
            topLeft = Offset(boxLeft, boxTop),
            size = Size(boxRight - boxLeft, (height - boxTop)),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
        )

        // 2. Goal Dimensions (in screen coordinates)
        // Standard goal width: 7.32m, height: 2.44m
        val goalWidthPx = width * 0.68f
        val goalHeightPx = height * 0.26f
        val goalLeft = (width - goalWidthPx) / 2f
        val goalRight = goalLeft + goalWidthPx
        val goalBottom = horizonY + goalHeightPx
        val goalTop = horizonY

        // Draw Net backing
        val netPath = Path().apply {
            moveTo(goalLeft, goalBottom)
            lineTo(goalLeft, goalTop)
            lineTo(goalRight, goalTop)
            lineTo(goalRight, goalBottom)
            close()
        }
        drawPath(netPath, Color(0xFF0F2027).copy(alpha = 0.8f))

        // Net Mesh grid lines
        val netColumns = 14
        val netRows = 8
        for (c in 1 until netColumns) {
            val nx = goalLeft + (goalWidthPx / netColumns) * c
            drawLine(
                color = Color.White.copy(alpha = 0.25f),
                start = Offset(nx, goalTop),
                end = Offset(nx, goalBottom),
                strokeWidth = 1.2f
            )
        }
        for (r in 1 until netRows) {
            val ny = goalTop + (goalHeightPx / netRows) * r
            drawLine(
                color = Color.White.copy(alpha = 0.25f),
                start = Offset(goalLeft, ny),
                end = Offset(goalRight, ny),
                strokeWidth = 1.2f
            )
        }

        // White Goal Posts and Crossbar
        val postThickness = 8f
        // Crossbar
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(goalLeft - postThickness, goalTop - postThickness),
            size = Size(goalWidthPx + postThickness * 2f, postThickness),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )
        // Left Post
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(goalLeft - postThickness, goalTop),
            size = Size(postThickness, goalHeightPx),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )
        // Right Post
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(goalRight, goalTop),
            size = Size(postThickness, goalHeightPx),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )

        // 3. Defensive Wall (if Free Kick mode)
        if (state.isFreeKick) {
            val wallY = horizonY + (height - horizonY) * 0.45f
            val wallW = width * 0.24f
            val wallH = 80f
            val wallX = width * 0.5f - wallW / 2f

            for (w in 0 until 3) {
                val dx = wallX + w * (wallW / 3f)
                // Defender body
                drawRoundRect(
                    color = Color(0xFFE11D48), // Red jersey
                    topLeft = Offset(dx + 4f, wallY - wallH * 0.7f),
                    size = Size(wallW / 3f - 8f, wallH * 0.7f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
                // Defender head
                drawCircle(
                    color = Color(0xFFFFD54F),
                    radius = 9f,
                    center = Offset(dx + (wallW / 6f), wallY - wallH * 0.82f)
                )
            }
            // "MUR À 9.15m" indicator
            val wallText = textMeasurer.measure(
                text = "MUR : 9.15 m",
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE047))
            )
            drawText(wallText, topLeft = Offset(wallX, wallY + 6f))
        }

        // 4. Draw Goalkeeper
        // Goalkeeper coordinates: x in [-3.66..3.66], y in [0..2.44]
        val gkScreenX = (width / 2f) + (state.keeperX / 3.66f) * (goalWidthPx / 2f)
        val gkScreenY = goalBottom - (state.keeperY / 2.44f) * goalHeightPx
        drawGoalkeeper(
            scope = this,
            x = gkScreenX,
            y = gkScreenY,
            decision = state.keeperDecision
        )

        // If player has predictive vision (from answering science challenge), show keeper dive preview
        if (state.hasPredictiveVision) {
            val predTargetX = (width / 2f) + (state.keeperDecision.targetX / 3.66f) * (goalWidthPx / 2f)
            val predTargetY = goalBottom - (state.keeperDecision.targetY / 2.44f) * goalHeightPx
            drawCircle(
                color = Color(0xFF00D2FF).copy(alpha = 0.4f),
                radius = 24f,
                center = Offset(predTargetX, predTargetY)
            )
            val predText = textMeasurer.measure(
                text = "PREDICTION IA",
                style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00D2FF))
            )
            drawText(predText, topLeft = Offset(predTargetX - predText.size.width / 2f, predTargetY - 32f))
        }

        // 5. Draw Ballistics Target Aim Reticle (when not in flight)
        if (!state.isBallInFlight && state.outcome == ShotOutcome.NONE) {
            // Projected point at goal z=0
            val projectedGoalX = (width / 2f) + (state.aimDirectionDeg / 20f) * (goalWidthPx / 2f) + (state.spinRpm * 1.5f)
            val projectedGoalY = goalBottom - (state.aimAngleDeg / 40f) * goalHeightPx

            // Crosshair
            drawCircle(
                color = Color(0xFFFF8A00).copy(alpha = 0.8f),
                radius = 16f,
                center = Offset(projectedGoalX, projectedGoalY),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
            )
            drawCircle(
                color = Color(0xFFFF8A00),
                radius = 3f,
                center = Offset(projectedGoalX, projectedGoalY)
            )

            // Trajectory guidelines
            val ballOriginX = width / 2f
            val ballOriginY = height * 0.85f
            drawLine(
                color = Color(0xFFFF8A00).copy(alpha = 0.35f),
                start = Offset(ballOriginX, ballOriginY),
                end = Offset(projectedGoalX, projectedGoalY),
                strokeWidth = 2f
            )
        }

        // 6. Draw Soccer Ball
        // Ball 3D projection:
        // z varies from distanceMeters down to 0 (at goal line)
        val zRatio = (state.ballZ / state.distanceMeters).coerceIn(0f, 1f) // 1.0 at start, 0.0 at goal
        val ballScreenY = goalBottom + (height * 0.85f - goalBottom) * zRatio - (state.ballY / 2.44f) * goalHeightPx * (1f - zRatio * 0.5f)
        val ballScreenX = (width / 2f) + (state.ballX / 3.66f) * (goalWidthPx / 2f) * (1f - zRatio * 0.3f)
        val ballRadius = 10f + 20f * zRatio

        // Ball shadow on grass
        drawOval(
            color = Color.Black.copy(alpha = 0.4f * zRatio),
            topLeft = Offset(ballScreenX - ballRadius * 0.9f, goalBottom + (height * 0.85f - goalBottom) * zRatio - 4f),
            size = Size(ballRadius * 1.8f, ballRadius * 0.7f)
        )

        // Ball Body
        drawCircle(
            color = Color.White,
            radius = ballRadius,
            center = Offset(ballScreenX, ballScreenY)
        )
        drawCircle(
            color = Color(0xFF1E293B),
            radius = ballRadius,
            center = Offset(ballScreenX, ballScreenY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
        )
        // Black pentagons on soccer ball
        val pAngle = if (state.isBallInFlight) state.spinRpm * 0.1f else 0f
        drawCircle(
            color = Color(0xFF1E293B),
            radius = ballRadius * 0.35f,
            center = Offset(ballScreenX + cos(pAngle) * ballRadius * 0.2f, ballScreenY + sin(pAngle) * ballRadius * 0.2f)
        )

        // Penalty spot on pitch
        drawCircle(
            color = Color.White.copy(alpha = 0.7f),
            radius = 5f,
            center = Offset(width / 2f, height * 0.85f)
        )
    }
}

private fun drawGoalkeeper(
    scope: DrawScope,
    x: Float,
    y: Float,
    decision: KeeperDecision
) {
    scope.apply {
        val keeperH = 68f
        val keeperW = 32f

        // Goalkeeper Body (Bright Yellow / Neon Green jersey)
        val jerseyColor = Color(0xFFFBBF24)

        // Head
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = 10f,
            center = Offset(x, y - keeperH * 0.4f)
        )

        // Torso
        drawRoundRect(
            color = jerseyColor,
            topLeft = Offset(x - keeperW * 0.45f, y - keeperH * 0.28f),
            size = Size(keeperW * 0.9f, keeperH * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )

        // Goalkeeper Gloves
        val gloveColor = Color(0xFFEF4444)
        drawCircle(
            color = gloveColor,
            radius = 7f,
            center = Offset(x - keeperW * 0.65f, y - keeperH * 0.1f)
        )
        drawCircle(
            color = gloveColor,
            radius = 7f,
            center = Offset(x + keeperW * 0.65f, y - keeperH * 0.1f)
        )

        // Shorts & Legs
        drawRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(x - keeperW * 0.4f, y + keeperH * 0.15f),
            size = Size(keeperW * 0.8f, keeperH * 0.25f)
        )
    }
}
