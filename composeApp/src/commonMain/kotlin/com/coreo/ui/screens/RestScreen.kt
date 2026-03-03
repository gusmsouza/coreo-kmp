package com.coreo.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
fun RestScreen(
    currentSet: Int,
    numberOfSets: Int,
    restDuration: Int,
    completedDuration: Int,
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    var timeRemaining       by remember { mutableStateOf(restDuration) }
    var totalDuration       by remember { mutableStateOf(restDuration) }
    var isRunning           by remember { mutableStateOf(true) }
    var restartTrigger      by remember { mutableStateOf(0) }

    // Progress animates from 1.0 down to 0.0 as rest counts down
    val progress = timeRemaining.toFloat() / totalDuration.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(durationMillis = 900),
        label         = "restProgress"
    )

    // Timer coroutine -- restarts when restartTrigger changes (for +10s)
    LaunchedEffect(restartTrigger) {
        isRunning = true
        while (timeRemaining > 0 && isRunning) {
            delay(1000L)
            timeRemaining--
        }
        if (isRunning && timeRemaining == 0) {
            onComplete()
        }
    }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .background(CoreoColors.Background)
            .padding(horizontal = CoreoSpacing.L),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        // Completed set header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.CheckCircle,
                contentDescription = null,
                tint               = CoreoColors.Success,
                modifier           = Modifier.size(50.dp)
            )
            Text(
                text       = "Serie $currentSet/$numberOfSets",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = CoreoColors.Text
            )
            Text(
                text  = "Tempo: ${completedDuration}s",
                style = CoreoType.Body,
                color = CoreoColors.TextSecondary
            )
        }

        Spacer(Modifier.weight(1f))

        // Rest timer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text       = "Descanse",
                fontSize   = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color      = CoreoColors.Text
            )

            Box(
                modifier         = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                ProgressCircle(
                    progress  = animatedProgress,
                    color     = CoreoColors.Accent,
                    modifier  = Modifier.fillMaxSize()
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = "$timeRemaining",
                        fontSize   = 100.sp,
                        fontWeight = FontWeight.Bold,
                        color      = CoreoColors.Accent
                    )
                    Text(
                        text  = "segundos",
                        style = CoreoType.Body,
                        color = CoreoColors.TextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Action buttons
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add 10s button
            OutlinedButton(
                onClick = {
                    timeRemaining += 10
                    totalDuration += 10
                    restartTrigger++
                },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(CoreoRadius.M),
                border   = androidx.compose.foundation.BorderStroke(
                    1.dp, CoreoColors.Primary
                )
            ) {
                Text(
                    text       = "+ Adicionar 10s",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = CoreoColors.Primary,
                    modifier   = Modifier.padding(vertical = 8.dp)
                )
            }

            // Skip rest button
            Button(
                onClick  = {
                    isRunning = false
                    onSkip()
                },
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = CoreoColors.Accent
                ),
                shape = RoundedCornerShape(CoreoRadius.M)
            ) {
                Icon(
                    imageVector        = Icons.Default.FastForward,
                    contentDescription = null,
                    tint               = CoreoColors.Background,
                    modifier           = Modifier.size(24.dp)
                )
                Text(
                    text       = "  Pular Descanso",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = CoreoColors.Background,
                    modifier   = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}