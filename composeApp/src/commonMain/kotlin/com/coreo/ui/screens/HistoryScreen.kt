package com.coreo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import com.coreo.model.WorkoutSession
import com.coreo.theme.CoreoColors
import com.coreo.theme.CoreoRadius
import com.coreo.theme.CoreoSpacing
import com.coreo.theme.CoreoType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    sessions: List<WorkoutSession>,
    onDeleteSession: (WorkoutSession) -> Unit,
    onBack: () -> Unit
) {
    var sessionToDelete by remember { mutableStateOf<WorkoutSession?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CoreoColors.Background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text       = "Histórico",
                    fontWeight = FontWeight.SemiBold,
                    color      = CoreoColors.Text
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint               = CoreoColors.Primary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CoreoColors.Background
            )
        )

        if (sessions.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Timer,
                        contentDescription = null,
                        tint               = CoreoColors.Primary.copy(alpha = 0.3f),
                        modifier           = Modifier.size(80.dp)
                    )
                    Text(
                        text       = "Nenhum Treino Ainda",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color      = CoreoColors.Text
                    )
                    Text(
                        text     = "Seu histórico aparecerá aqui após seus primeiros treinos.",
                        style    = CoreoType.Body,
                        color    = CoreoColors.TextSecondary,
                        modifier = Modifier.padding(horizontal = 40.dp)
                    )
                }
            }
        } else {
            val grouped = sessions
                .groupBy { session ->
                    session.date.toLocalDateTime(TimeZone.currentSystemDefault()).date
                }
                .entries
                .sortedByDescending { it.key }

            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(horizontal = CoreoSpacing.L, vertical = CoreoSpacing.M),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grouped.forEach { (date, daySessions) ->
                    item {
                        Text(
                            text       = formatSectionDate(date.toString()),
                            style      = CoreoType.Caption,
                            color      = CoreoColors.TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            modifier   = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(items = daySessions, key = { it.id }) { session ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    sessionToDelete = session
                                }
                                false
                            }
                        )

                        SwipeToDismissBox(
                            state                    = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent        = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(CoreoRadius.M))
                                        .background(CoreoColors.Error.copy(alpha = 0.15f))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector        = Icons.Default.Delete,
                                        contentDescription = "Deletar",
                                        tint               = CoreoColors.Error
                                    )
                                }
                            }
                        ) {
                            WorkoutRow(session = session)
                        }
                    }
                }
            }
        }
    }

    sessionToDelete?.let { session ->
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = { Text("Deletar Treino") },
            text  = {
                Text(
                    if (session.isSet)
                        "Tem certeza que deseja deletar este set de ${session.completedReps} series?"
                    else
                        "Tem certeza que deseja deletar este treino de ${session.duration}s?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteSession(session)
                    sessionToDelete = null
                }) {
                    Text("Deletar", color = CoreoColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
                    Text("Cancelar", color = CoreoColors.Primary)
                }
            }
        )
    }
}

@Composable
private fun WorkoutRow(session: WorkoutSession) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CoreoRadius.M))
            .background(
                if (session.isSet) CoreoColors.Accent.copy(alpha = 0.05f)
                else CoreoColors.PrimaryLight.copy(alpha = 0.3f)
            )
            .padding(16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    if (session.isSet) CoreoColors.Accent.copy(alpha = 0.15f)
                    else CoreoColors.Primary.copy(alpha = 0.15f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = if (session.isSet) Icons.Default.Repeat else Icons.Default.Timer,
                contentDescription = null,
                tint               = if (session.isSet) CoreoColors.Accent else CoreoColors.Primary,
                modifier           = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (session.isSet) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text       = "${session.completedReps}x series",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = CoreoColors.Text
                    )
                    Text(
                        text  = "(${formatTime(session.totalExerciseTime)})",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = CoreoColors.Accent
                    )
                }
                Text(
                    text  = session.durations.joinToString(" • ") { "${it}s" },
                    style = CoreoType.Caption,
                    color = CoreoColors.TextSecondary
                )
            } else {
                Text(
                    text       = "${session.duration}s",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = CoreoColors.Text
                )
                Text(
                    text  = "Prancha simples",
                    style = CoreoType.Caption,
                    color = CoreoColors.TextSecondary
                )
            }
        }

        Text(
            text  = formatTimeOfDay(session),
            style = CoreoType.Caption,
            color = CoreoColors.TextSecondary
        )
    }
}

private fun formatSectionDate(dateStr: String): String {
    val parts = dateStr.split("-")
    if (parts.size != 3) return dateStr
    val months = listOf(
        "Janeiro", "Fevereiro", "Marco", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )
    val month = parts[1].toIntOrNull()?.let { months.getOrNull(it - 1) } ?: parts[1]
    return "${parts[2]} de $month"
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return if (m > 0) "${m}m ${s}s" else "${s}s"
}

private fun formatTimeOfDay(session: WorkoutSession): String {
    val dt  = session.date.toLocalDateTime(TimeZone.currentSystemDefault())
    val h   = dt.hour.toString().padStart(2, '0')
    val min = dt.minute.toString().padStart(2, '0')
    return "$h:$min"
}
