package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InteractiveLabType
import com.example.model.LessonRepository
import com.example.model.LessonTopic
import com.example.model.Subject
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LabRevisionScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember { mutableStateOf(Subject.MECANIQUE) }
    val topics = remember(selectedSubject) {
        LessonRepository.topics.filter { it.subject == selectedSubject }
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
                    .testTag("lab_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Labo & Fiches de Révision",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    "Formules, simulateurs & théorie interactive",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // Subject Selector Tabs (Mécanique, Programmation, Maths)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(Subject.MECANIQUE, Subject.PROGRAMMATION, Subject.MATHS).forEach { subj ->
                val isSelected = (selectedSubject == subj)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) subj.color else Color(0xFF1E293B),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedSubject = subj }
                        .testTag("tab_${subj.name.lowercase()}"),
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(subj.icon, fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            subj.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Topic Cards with Interactive Simulators
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(topics, key = { it.id }) { topic ->
                TopicCard(topic = topic)
            }
            item {
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TopicCard(topic: LessonTopic) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
        border = androidx.compose.foundation.BorderStroke(1.dp, topic.subject.color.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(topic.subject.icon, fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(topic.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(topic.subtitle, fontSize = 12.sp, color = Color(0xFF94A3B8))
                }
            }

            Spacer(Modifier.height(12.dp))

            // Key Points
            topic.keyPoints.forEach { point ->
                Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                    Text("•", color = topic.subject.color, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(8.dp))
                    Text(point, fontSize = 13.sp, color = Color(0xFFE2E8F0), lineHeight = 18.sp)
                }
            }

            Spacer(Modifier.height(10.dp))

            // Formula Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topic.formulas.forEach { (label, formula) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                            Text(label, fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(
                                formula,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Interactive Simulator Box
            InteractiveSimulatorView(type = topic.interactiveType, subjectColor = topic.subject.color)
        }
    }
}

@Composable
private fun InteractiveSimulatorView(type: InteractiveLabType, subjectColor: Color) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Science, contentDescription = null, tint = subjectColor, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("LABORATOIRE INTERACTIF", fontSize = 11.sp, fontWeight = FontWeight.Black, color = subjectColor)
            }

            Spacer(Modifier.height(8.dp))

            when (type) {
                InteractiveLabType.FRICTION_STOPPING_DISTANCE -> {
                    // Mécanique: Distance de freinage d = v² / (2*mu*g)
                    var speedKmh by remember { mutableFloatStateOf(100f) }
                    var mu by remember { mutableFloatStateOf(0.8f) }
                    val speedMs = speedKmh / 3.6f
                    val stoppingDist = (speedMs * speedMs) / (2f * mu * 9.81f)
                    val kineticEnergyKj = 0.5f * 1200f * (speedMs * speedMs) / 1000f // 1200 kg car

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Vitesse : ${speedKmh.toInt()} km/h", fontSize = 12.sp, color = Color.White)
                            Text("Adhérence μ : ${"%.2f".format(mu)}", fontSize = 12.sp, color = Color(0xFFFF8A00))
                        }
                        Slider(
                            value = speedKmh,
                            onValueChange = { speedKmh = it },
                            valueRange = 40f..220f,
                            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = subjectColor)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0.8f to "Asphalte sec", 0.4f to "Pluie", 0.1f to "Verglas").forEach { (m, name) ->
                                FilledTonalButton(
                                    onClick = { mu = m },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = if (mu == m) subjectColor else Color(0xFF1E293B)
                                    )
                                ) {
                                    Text(name, fontSize = 10.sp, color = if (mu == m) Color.Black else Color.White)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Distance d'arrêt : ${"%.1f".format(stoppingDist)} m | Énergie cinétique : ${kineticEnergyKj.toInt()} kJ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                InteractiveLabType.BOOLEAN_LOGIC_CIRCUIT -> {
                    // Programmation: Portes booléennes A, B, AND/OR/XOR
                    var inputA by remember { mutableStateOf(true) }
                    var inputB by remember { mutableStateOf(false) }
                    var operator by remember { mutableStateOf("AND") }

                    val result = when (operator) {
                        "AND" -> inputA && inputB
                        "OR" -> inputA || inputB
                        "XOR" -> inputA xor inputB
                        else -> !(inputA && inputB)
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("A :", color = Color.White, fontSize = 13.sp)
                                Spacer(Modifier.width(4.dp))
                                Switch(checked = inputA, onCheckedChange = { inputA = it })
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("B :", color = Color.White, fontSize = 13.sp)
                                Spacer(Modifier.width(4.dp))
                                Switch(checked = inputB, onCheckedChange = { inputB = it })
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("AND", "OR", "XOR", "NAND").forEach { op ->
                                FilledTonalButton(
                                    onClick = { operator = op },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = if (operator == op) Color(0xFF00D2FF) else Color(0xFF1E293B)
                                    )
                                ) {
                                    Text(op, fontSize = 10.sp, color = if (operator == op) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Résultat de ($inputA $operator $inputB) = $result",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (result) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                InteractiveLabType.BALLISTIC_TRAJECTORY -> {
                    // Maths: Trajectoire balistique
                    var angleDeg by remember { mutableFloatStateOf(45f) }
                    var v0 by remember { mutableFloatStateOf(25f) }

                    val thetaRad = Math.toRadians(angleDeg.toDouble()).toFloat()
                    val rangeX = (v0 * v0 * sin(2 * thetaRad)) / 9.81f
                    val maxHeight = (v0 * v0 * sin(thetaRad) * sin(thetaRad)) / (2 * 9.81f)

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Angle : ${angleDeg.toInt()}°", fontSize = 12.sp, color = Color.White)
                            Text("Vitesse : ${v0.toInt()} m/s", fontSize = 12.sp, color = Color(0xFF10B981))
                        }
                        Slider(
                            value = angleDeg,
                            onValueChange = { angleDeg = it },
                            valueRange = 15f..75f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFF10B981), activeTrackColor = Color(0xFF10B981))
                        )
                        Text(
                            "Portée X_max : ${"%.1f".format(rangeX)} m | Hauteur max H : ${"%.1f".format(maxHeight)} m",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }

                InteractiveLabType.ALGO_SEARCH_SPEED -> {
                    // Programmation: Comparateur O(N) vs O(log N)
                    var arraySize by remember { mutableFloatStateOf(1000f) }
                    val linearSteps = arraySize.toInt()
                    val binarySteps = (kotlin.math.log2(arraySize) + 1).toInt()

                    Column {
                        Text("Taille du tableau trié N : ${arraySize.toInt()}", fontSize = 12.sp, color = Color.White)
                        Slider(
                            value = arraySize,
                            onValueChange = { arraySize = it },
                            valueRange = 100f..100000f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFF00D2FF), activeTrackColor = Color(0xFF00D2FF))
                        )
                        Text(
                            "Recherche linéaire O(N) : $linearSteps étapes",
                            fontSize = 12.sp,
                            color = Color(0xFFEF4444)
                        )
                        Text(
                            "Recherche dichotomique O(log N) : seulement $binarySteps étapes !",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                InteractiveLabType.TRIGONOMETRY_CIRCLE -> {
                    // Maths: Cercle trigo sin, cos, tan
                    var angleDeg by remember { mutableFloatStateOf(30f) }
                    val rad = Math.toRadians(angleDeg.toDouble())
                    val cosVal = cos(rad)
                    val sinVal = sin(rad)

                    Column {
                        Text("Angle θ : ${angleDeg.toInt()}° (${"%.2f".format(rad)} rad)", fontSize = 12.sp, color = Color.White)
                        Slider(
                            value = angleDeg,
                            onValueChange = { angleDeg = it },
                            valueRange = 0f..90f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFF10B981), activeTrackColor = Color(0xFF10B981))
                        )
                        Text(
                            "cos(θ) = ${"%.3f".format(cosVal)} | sin(θ) = ${"%.3f".format(sinVal)} | cos² + sin² = 1.000",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                InteractiveLabType.MAGNUS_EFFECT_CURVE -> {
                    // Mécanique: Effet Magnus
                    var spin by remember { mutableFloatStateOf(15f) }
                    Column {
                        Text("Vitesse de rotation de la balle (Spin) : ${spin.toInt()} rad/s", fontSize = 12.sp, color = Color.White)
                        Slider(
                            value = spin,
                            onValueChange = { spin = it },
                            valueRange = -30f..30f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFFFF8A00), activeTrackColor = Color(0xFFFF8A00))
                        )
                        Text(
                            if (spin > 5) "Trajectoire courbée vers la droite (dépression à droite)"
                            else if (spin < -5) "Trajectoire courbée vers la gauche (dépression à gauche)"
                            else "Trajectoire balistique rectiligne sans effet",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00D2FF)
                        )
                    }
                }
            }
        }
    }
}
