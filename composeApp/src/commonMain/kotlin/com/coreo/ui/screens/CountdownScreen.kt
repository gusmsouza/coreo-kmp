package com.coreo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coreo.theme.CoreoColors
import com.coreo.theme.CoreoType
import kotlinx.coroutines.delay

@Composable
fun CountdownScreen(
    currentSet: Int,
    numberOfSets: Int,
    durationPerSet: Int,
    onComplete: () -> Unit
) {
    var countdownNumber by remember { mutableIntStateOf(3) }

    // Countdown coroutine -- runs once, fires onComplete when done
    LaunchedEffect(Unit) {
        while (countdownNumber > 0) {
            delay(1000L)
            countdownNumber--
        }
        // Small pause at 0 before transitioning
        delay(400L)
        onComplete()
    }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .background(CoreoColors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top label
        Column(
            modifier            = Modifier.padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text     = "Prepare-se",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color    = CoreoColors.TextSecondary
            )
            if (numberOfSets > 1) {
                Text(
                    text       = "Série $currentSet/$numberOfSets",
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color      = CoreoColors.Accent
                )
            }
        }

        // Large countdown number
        Text(
            text       = if (countdownNumber > 0) "$countdownNumber" else "Vai!",
            fontSize   = 180.sp,
            fontWeight = FontWeight.Bold,
            color      = CoreoColors.Accent
        )

        // Bottom info
        Column(
            modifier            = Modifier.padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text     = "${durationPerSet}s de prancha",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color    = CoreoColors.Text
            )
        }
    }
}