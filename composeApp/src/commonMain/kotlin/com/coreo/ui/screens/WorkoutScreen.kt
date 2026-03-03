package com.coreo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coreo.theme.CoreoColors
import com.coreo.theme.CoreoRadius
import com.coreo.theme.CoreoSpacing
import com.coreo.theme.CoreoType
import com.coreo.ui.components.ProgressCircle
import kotlinx.coroutines.delay

@Composable
fun WorkoutScreen(
    currentSet: Int,
    numberOfSets: Int,
    durationPerSet: Int,
    onComplete: (duration: Int) -> Unit,
    onCancel: () -> Unit
) {
    var timeRemaining    by remember { mutableIntStateOf(durationPerSet) }
    var showConfirmation by remember { mutableStateOf(false) }
    var completedFull    by remember { mutableStateOf(false) }
    var actualDuration   by remember { mutableIntStateOf(0) }
    var currentMessage   by remember { mutableStateOf("") }
    var timerRunning     by remember { mutableStateOf(true) }

    // Smooth progress: updated at ~60fps using elapsed time
    // Completely independent of the 1-second timer ticks
    var smoothProgress by remember { mutableFloatStateOf(0f) }
    var startTimeMs    by remember { mutableStateOf(System.currentTimeMillis()) }

    // 60fps progress updater
    LaunchedEffect(Unit) {
        val totalMs = durationPerSet * 1000L
        while (smoothProgress < 1f && timerRunning) {
            val elapsed = System.currentTimeMillis() - startTimeMs
            smoothProgress = (elapsed.toFloat() / totalMs).coerceIn(0f, 1f)
            delay(16L)
        }
    }

    // 1-second countdown timer + auto-complete
    LaunchedEffect(Unit) {
        while (timeRemaining > 0 && timerRunning) {
            delay(1000L)
            if (!timerRunning) break
            timeRemaining--

            // Update motivational message
            val elapsed  = durationPerSet - timeRemaining
            val progress = elapsed.toDouble() / durationPerSet.toDouble()
            val shouldShow = elapsed == 1 || (elapsed > 5 && elapsed % 10 == 0)

            if (shouldShow && timeRemaining > 5) {
                currentMessage = if (progress <= 0.5) {
                    listOf(
                        "Respire com calma",
                        "Corpo alinhado, do ombro ao tornozelo",
                        "Core firme e engajado",
                        "Mantenha o olhar no chao",
                        "Cotovelos logo abaixo dos ombros",
                        "Quadril neutro, nao deixe cair"
                    ).random()
                } else {
                    when {
                        timeRemaining <= 10 -> "So mais ${timeRemaining}s! Voce consegue!"
                        timeRemaining <= 20 -> listOf(
                            "Forca! Esta quase!",
                            "Continue! Voce esta indo muito bem!",
                            "Firme! A reta final chegou!"
                        ).random()
                        else -> listOf(
                            "Voce esta forte!",
                            "Progresso consistente!",
                            "Respiracao calma, corpo firme"
                        ).random()
                    }
                }
            }
        }

        // Auto-complete when timer reaches zero
        if (timerRunning && timeRemaining == 0) {
            completedFull    = true
            actualDuration   = durationPerSet
            timerRunning     = false
            smoothProgress   = 1f
            currentMessage   = "Excelente!"
            delay(500L)
            showConfirmation = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CoreoColors.Background)
    ) {
        Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cancel button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CoreoSpacing.M, vertical = CoreoSpacing.S),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    timerRunning = false
                    onCancel()
                }) {
                    Icon(
                        imageVector        = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint               = CoreoColors.Primary,
                        modifier           = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Cancelar", color = CoreoColors.Primary, fontSize = 17.sp)
                }
            }

            Spacer(Modifier.weight(1f))

            // Timer + smooth progress circle
            Box(
                modifier         = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                ProgressCircle(
                    progress = smoothProgress,
                    color    = CoreoColors.Primary,
                    modifier = Modifier.fillMaxSize()
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = "$timeRemaining",
                        fontSize   = 120.sp,
                        fontWeight = FontWeight.Bold,
                        color      = CoreoColors.Accent
                    )
                    if (numberOfSets > 1) {
                        Text(
                            text       = "Serie $currentSet/$numberOfSets",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = CoreoColors.TextSecondary
                        )
                    } else {
                        Text(
                            text  = "segundos",
                            fontSize = 24.sp,
                            color = CoreoColors.TextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Motivational message
            Box(modifier = Modifier.height(80.dp), contentAlignment = Alignment.Center) {
                if (currentMessage.isNotEmpty()) {
                    Text(
                        text       = currentMessage,
                        fontSize   = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = CoreoColors.Text,
                        modifier   = Modifier.padding(horizontal = 30.dp)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Stop button
            Button(
                onClick = {
                    timerRunning   = false
                    actualDuration = durationPerSet - timeRemaining
                    completedFull  = false
                    showConfirmation = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoreoColors.Primary),
                shape  = RoundedCornerShape(CoreoRadius.L)
            ) {
                Text(
                    text       = "Parar",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = CoreoColors.Background,
                    modifier   = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(40.dp))
        }

        // Confirmation overlay
        if (showConfirmation) {
            ConfirmationOverlay(
                completedFull  = completedFull,
                actualDuration = actualDuration,
                durationPerSet = durationPerSet,
                currentSet     = currentSet,
                numberOfSets   = numberOfSets,
                onConfirm = {
                    val toSave = if (completedFull) durationPerSet else actualDuration
                    onComplete(toSave)
                },
                onDiscard = onCancel
            )
        }
    }
}

// MARK: - Confirmation Overlay

@Composable
private fun ConfirmationOverlay(
    completedFull: Boolean,
    actualDuration: Int,
    durationPerSet: Int,
    currentSet: Int,
    numberOfSets: Int,
    onConfirm: () -> Unit,
    onDiscard: () -> Unit
) {
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(CoreoColors.Text.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier            = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Text(
                text       = if (completedFull) "Confirmacao" else "Treino Incompleto",
                style      = CoreoType.Headline,
                color      = CoreoColors.Background,
                fontWeight = FontWeight.Bold
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (completedFull) {
                    Text(
                        text  = if (numberOfSets > 1) "Serie $currentSet/$numberOfSets completada!"
                        else "Voce completou ${durationPerSet}s de prancha",
                        style = CoreoType.H3,
                        color = CoreoColors.Background.copy(alpha = 0.9f)
                    )
                    Text(
                        text  = "Realmente fez o exercicio?",
                        style = CoreoType.Body,
                        color = CoreoColors.Background.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        text  = "Voce fez ${actualDuration}s de ${durationPerSet}s",
                        style = CoreoType.H3,
                        color = CoreoColors.Background.copy(alpha = 0.9f)
                    )
                    Text(
                        text  = "Deseja registrar mesmo assim?",
                        style = CoreoType.Body,
                        color = CoreoColors.Background.copy(alpha = 0.7f)
                    )
                }
            }

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Button(
                    onClick  = onDiscard,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = CoreoColors.Primary.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(CoreoRadius.M)
                ) {
                    Text(
                        text       = "Nao, descartar",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = CoreoColors.Background,
                        modifier   = Modifier.padding(vertical = 8.dp)
                    )
                }
                Button(
                    onClick  = onConfirm,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = CoreoColors.Accent
                    ),
                    shape = RoundedCornerShape(CoreoRadius.M)
                ) {
                    Text(
                        text       = "Sim, registrar",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = CoreoColors.Text,
                        modifier   = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}