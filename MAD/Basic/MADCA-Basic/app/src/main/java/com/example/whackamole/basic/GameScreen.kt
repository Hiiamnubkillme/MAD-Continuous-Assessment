// Basic/GameScreen.kt
package com.example.whackamole.basic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController) {
    val context = LocalContext.current
    val highScoreManager = remember { HighScoreManager(context) }
    var score by remember { mutableStateOf(0) }
    var highScore by remember { mutableStateOf(highScoreManager.getHighScore()) }
    var timeLeft by remember { mutableStateOf(30) } // 30 seconds
    var molePosition by remember { mutableStateOf(Random.nextInt(9)) }
    var isGameRunning by remember { mutableStateOf(false) }
    var showGameOver by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun startGame() {
        score = 0
        timeLeft = 30
        isGameRunning = true
        showGameOver = false
        coroutineScope.launch {
            while (isGameRunning && timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft <= 0) {
                isGameRunning = false
                showGameOver = true
                if (score > highScore) {
                    highScore = score
                    highScoreManager.saveHighScore(score)
                }
            }
        }
        coroutineScope.launch {
            while (isGameRunning) {
                delay(Random.nextLong(700, 1001))
                molePosition = Random.nextInt(9)
            }
        }
    }

    fun onHoleClick(index: Int) {
        if (isGameRunning && index == molePosition) {
            score++
            molePosition = Random.nextInt(9) // Move mole immediately after hit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Whack-a-Mole") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("Score: $score", fontSize = 20.sp)
                Text("Time: $timeLeft", fontSize = 20.sp)
                Text("High: $highScore", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.size(300.dp)
            ) {
                items(9) { index ->
                    Button(
                        onClick = { onHoleClick(index) },
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (index == molePosition && isGameRunning) Color.Green else Color.Gray
                        )
                    ) {
                        if (index == molePosition && isGameRunning) {
                            Text("🐭", fontSize = 32.sp) // Mole emoji for unique UI
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { startGame() }) {
                Text(if (isGameRunning) "Restart" else "Start")
            }
            if (showGameOver) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Game Over! Final Score: $score", fontSize = 24.sp, textAlign = TextAlign.Center, color = Color.Red)
            }
        }
    }
}