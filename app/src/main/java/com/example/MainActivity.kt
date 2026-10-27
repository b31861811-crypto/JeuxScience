package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.game.foot.FootScreen
import com.example.game.race.RaceScreen
import com.example.model.Subject
import com.example.ui.HomeScreen
import com.example.ui.LabRevisionScreen
import com.example.ui.ProfileScreen
import com.example.ui.theme.MyApplicationTheme

enum class Screen {
    HOME,
    RACE,
    FOOT,
    LAB,
    PROFILE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = GameRepository(database.userStatsDao(), database.gameHistoryDao())

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF070B19)
                ) {
                    MainAppNavigation(repository = repository)
                }
            }
        }
    }
}

@Composable
fun MainAppNavigation(repository: GameRepository) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var currentSubject by remember { mutableStateOf(Subject.MIXTE) }

    // Handle back button on Android
    BackHandler(enabled = currentScreen != Screen.HOME) {
        currentScreen = Screen.HOME
    }

    Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
        when (screen) {
            Screen.HOME -> {
                HomeScreen(
                    repository = repository,
                    onStartRace = { subject ->
                        currentSubject = subject
                        currentScreen = Screen.RACE
                    },
                    onStartFoot = { subject ->
                        currentSubject = subject
                        currentScreen = Screen.FOOT
                    },
                    onOpenLab = {
                        currentScreen = Screen.LAB
                    },
                    onOpenProfile = {
                        currentScreen = Screen.PROFILE
                    }
                )
            }
            Screen.RACE -> {
                RaceScreen(
                    subject = currentSubject,
                    repository = repository,
                    onBack = { currentScreen = Screen.HOME }
                )
            }
            Screen.FOOT -> {
                FootScreen(
                    subject = currentSubject,
                    repository = repository,
                    onBack = { currentScreen = Screen.HOME }
                )
            }
            Screen.LAB -> {
                LabRevisionScreen(
                    onBack = { currentScreen = Screen.HOME }
                )
            }
            Screen.PROFILE -> {
                ProfileScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.HOME }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

