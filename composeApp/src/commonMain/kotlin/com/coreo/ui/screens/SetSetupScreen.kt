package com.coreo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coreo.model.UserSettings
import com.coreo.model.WorkoutSession
import com.coreo.theme.CoreoColors
import com.coreo.theme.CoreoRadius
import com.coreo.theme.CoreoSpacing
import com.coreo.theme.CoreoType
import com.coreo.ui.components.CoreoLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetSetupScreen(
    settings: UserSettings,
    lastSession: WorkoutSession?,
    onStartWorkout: (sets: Int, duration: Int, rest: Int) -> Unit,
    onCancel: () -> Unit
) {
    // Load defaults from last session (mirrors SetSetupView.loadDefaults())
    val defaultSets     = if (lastSession?.isSet == true) lastSession.totalReps else 1
    val defaultDuration = when {
        lastSession == null        -> settings.defaultDuration
        lastSession.isSet          -> lastSession.durations.firstOrNull() ?: settings.defaultDuration
        else                       -> lastSession.duration
    }
    val defaultRest = if (lastSession?.isSet == true) lastSession.restDuration
    else settings.defaultRestDuration

    var numberOfSets   by remember { mutableStateOf(defaultSets) }
    var durationPerSet by remember { mutableStateOf(defaultDuration) }
    var restDuration   by remember { mutableStateOf(defaultRest) }

    val isValid = numberOfSets in 1..10 &&
            durationPerSet in 10..300 &&
            restDuration in 15..180

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CoreoColors.Background)
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector        = Icons.Default.ArrowBack,
                        contentDescription = "Cancelar",
                        tint               = CoreoColors.Primary
                    )
                }
            },
            actions = {
                TextButton(onClick = onCancel) {
                    Text(text = "Cancelar", color = CoreoColors.Primary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CoreoColors.Background
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = CoreoSpacing.L),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CoreoLogo(size = 60.dp)
                Text(
                    text       = "Configurar Treino",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = CoreoColors.Text
                )
            }

            // Series config card
            ConfigCard(
                title      = "Séries",
                icon       = Icons.Default.Refresh,
                value      = numberOfSets,
                min        = 1,
                max        = 10,
                step       = 1,
                suffix     = if (numberOfSets == 1) "série" else "séries",
                onIncrement = { numberOfSets++ },
                onDecrement = { numberOfSets-- }
            )

            // Duration config card
            ConfigCard(
                title       = "Duração",
                icon        = Icons.Default.Timer,
                value       = durationPerSet,
                min         = 10,
                max         = 300,
                step        = 5,
                suffix      = "s por série",
                onIncrement = { durationPerSet += 5 },
                onDecrement = { durationPerSet -= 5 }
            )

            // Rest config card (only if multiple sets)
            if (numberOfSets > 1) {
                ConfigCard(
                    title       = "Intervalo",
                    icon        = Icons.Default.Pause,
                    value       = restDuration,
                    min         = 15,
                    max         = 180,
                    step        = 5,
                    suffix      = "s de descanso",
                    onIncrement = { restDuration += 5 },
                    onDecrement = { restDuration -= 5 }
                )
            }

            // Summary card
            SummaryCard(
                numberOfSets   = numberOfSets,
                durationPerSet = durationPerSet,
                restDuration   = restDuration
            )

            // Start button
            Button(
                onClick  = { onStartWorkout(numberOfSets, durationPerSet, restDuration) },
                enabled  = isValid,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = CoreoColors.Accent,
                    disabledContainerColor = CoreoColors.PrimaryLight
                ),
                shape = RoundedCornerShape(CoreoRadius.L)
            ) {
                Text(
                    text       = "Começar Treino",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = CoreoColors.Background,
                    modifier   = Modifier.padding(vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// MARK: - Config Card

@Composable
private fun ConfigCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Int,
    min: Int,
    max: Int,
    step: Int,
    suffix: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CoreoRadius.L))
            .background(CoreoColors.PrimaryLight)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = CoreoColors.Accent,
                modifier           = Modifier.size(18.dp)
            )
            Text(text = title, style = CoreoType.H3, color = CoreoColors.Text)
        }

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            IconButton(
                onClick  = { if (value > min) onDecrement() },
                enabled  = value > min,
                modifier = Modifier.size(56.dp)
            ) {
                Text(
                    text      = "-",
                    fontSize  = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color     = if (value > min) CoreoColors.Primary else CoreoColors.PrimaryLight
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = "$value",
                    fontSize   = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color      = CoreoColors.Accent
                )
                Text(text = suffix, style = CoreoType.Caption, color = CoreoColors.TextSecondary)
            }

            IconButton(
                onClick  = { if (value < max) onIncrement() },
                enabled  = value < max,
                modifier = Modifier.size(56.dp)
            ) {
                Text(
                    text       = "+",
                    fontSize   = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (value < max) CoreoColors.Primary else CoreoColors.PrimaryLight
                )
            }
        }
    }
}

// MARK: - Summary Card

@Composable
private fun SummaryCard(
    numberOfSets: Int,
    durationPerSet: Int,
    restDuration: Int
) {
    val exerciseTotal = durationPerSet * numberOfSets
    val restTotal     = if (numberOfSets > 1) restDuration * (numberOfSets - 1) else 0
    val grandTotal    = exerciseTotal + restTotal

    fun formatTime(s: Int): String {
        val m = s / 60; val sec = s % 60
        return if (m > 0) "${m}m ${sec}s" else "${sec}s"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CoreoRadius.L))
            .background(CoreoColors.PrimaryLight)
            .border(1.dp, CoreoColors.Success.copy(alpha = 0.3f), RoundedCornerShape(CoreoRadius.L))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.BarChart,
                contentDescription = null,
                tint               = CoreoColors.Success,
                modifier           = Modifier.size(18.dp)
            )
            Text(text = "Resumo do Treino", style = CoreoType.H3, color = CoreoColors.Text)
        }

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Tempo de exercício:", style = CoreoType.Body, color = CoreoColors.TextSecondary)
            Text(
                text       = if (numberOfSets == 1) "${durationPerSet}s"
                else "${numberOfSets}x ${durationPerSet}s = ${formatTime(exerciseTotal)}",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = CoreoColors.Text
            )
        }

        if (numberOfSets > 1) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Tempo de descanso:", style = CoreoType.Body, color = CoreoColors.TextSecondary)
                Text(
                    text       = formatTime(restTotal),
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = CoreoColors.Text
                )
            }
        }

        Divider(color = CoreoColors.PrimaryLight)

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Tempo total:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CoreoColors.Text)
            Text(
                text       = formatTime(grandTotal),
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = CoreoColors.Accent
            )
        }
    }
}