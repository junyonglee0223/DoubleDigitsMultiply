package com.example.doubledigitsmultiply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.doubledigitsmultiply.ui.theme.DoubleDigitsMultiplyTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoubleDigitsMultiplyTheme {
                MultiplyGame()
            }
        }
    }
}

@Composable
fun MultiplyGame() {
    var num1 by remember { mutableStateOf(Random.nextInt(10, 99)) }
    var num2 by remember { mutableStateOf(Random.nextInt(10, 99)) }
    var userAnswer by remember { mutableStateOf(TextFieldValue("")) }
    var currentQuestion by remember { mutableStateOf(1) }
    var totalQuestions by remember { mutableStateOf(10) }
    var elapsedTime by remember { mutableStateOf(0.0) }
    var elapsedTimes by remember { mutableStateOf(mutableListOf<Double>()) }
    var showPlayAgainDialog by remember { mutableStateOf(false) }

    // Timer: Updates elapsedTime in real time
    LaunchedEffect(currentQuestion) {
        elapsedTime = 0.0
        while (currentQuestion <= totalQuestions && !showPlayAgainDialog) {
            delay(10) // Update every 10 milliseconds
            elapsedTime += 0.01
        }
    }

    if (showPlayAgainDialog) {
        PlayAgainDialog(
            elapsedTimes = elapsedTimes,
            onPlayAgain = {
                // Reset the game
                num1 = Random.nextInt(10, 99)
                num2 = Random.nextInt(10, 99)
                userAnswer = TextFieldValue("")
                currentQuestion = 1
                elapsedTimes.clear()
                showPlayAgainDialog = false
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Question $currentQuestion of $totalQuestions",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "$num1 x $num2 = ?",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = userAnswer,
                onValueChange = { userAnswer = it },
                label = { Text("Enter your answer") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number, // 숫자 키보드 설정
                    imeAction = ImeAction.Done // 완료 버튼 활성화
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val correctAnswer = num1 * num2
                        if (userAnswer.text.toIntOrNull() == correctAnswer) {
                            elapsedTimes.add(elapsedTime)
                            if (currentQuestion < totalQuestions) {
                                currentQuestion++
                                num1 = Random.nextInt(10, 99)
                                num2 = Random.nextInt(10, 99)
                                userAnswer = TextFieldValue("")
                            } else {
                                showPlayAgainDialog = true
                            }
                        } else {
                            userAnswer = TextFieldValue("")
                        }
                    }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Elapsed Time: ${String.format("%.2f", elapsedTime)} seconds",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val correctAnswer = num1 * num2
                    if (userAnswer.text.toIntOrNull() == correctAnswer) {
                        // Record elapsed time
                        elapsedTimes.add(elapsedTime)

                        // Reset for next question
                        if (currentQuestion < totalQuestions) {
                            currentQuestion++
                            num1 = Random.nextInt(10, 99)
                            num2 = Random.nextInt(10, 99)
                            userAnswer = TextFieldValue("")
                        } else {
                            // End game
                            showPlayAgainDialog = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun PlayAgainDialog(elapsedTimes: List<Double>, onPlayAgain: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = "Game Over")
        },
        text = {
            Column {
                Text("Do you want to play again?")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Times for each question:")
                elapsedTimes.forEachIndexed { index, time ->
                    Text("Question ${index + 1}: ${String.format("%.2f", time)} seconds")
                }
            }
        },
        confirmButton = {
            Button(onClick = onPlayAgain) {
                Text("Play Again")
            }
        },
        dismissButton = {
            Button(onClick = { /* Close the app or navigate away */ }) {
                Text("Exit")
            }
        }
    )
}
