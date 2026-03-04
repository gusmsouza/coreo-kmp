package com.coreo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coreo.model.DayType
import com.coreo.model.UserSettings
import com.coreo.model.WorkoutSession
import com.coreo.repository.CalendarDay
import com.coreo.repository.DayData
import com.coreo.repository.WorkoutRepository
import com.coreo.theme.CoreoColors
import com.coreo.theme.CoreoRadius
import com.coreo.theme.CoreoSpacing
import com.coreo.theme.CoreoType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    sessions: List<WorkoutSession>,
    settings: UserSettings,
    onBack: () -> Unit
) {
    val repo = WorkoutRepository()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CoreoColors.Background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text       = "Estatísticas",
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

        if (sessions.size < 3) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier            = Modifier.padding(horizontal = 40.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.BarChart,
                        contentDescription = null,
                        tint               = CoreoColors.Primary.copy(alpha = 0.3f),
                        modifier           = Modifier.size(80.dp)
                    )
                    Text(
                        text       = if (sessions.isEmpty()) "Nenhum Dado Ainda" else "Continue Treinando",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color      = CoreoColors.Text
                    )
                    Text(
                        text  = if (sessions.isEmpty())
                            "Suas estatísticas aparecerão aqui após seus primeiros treinos."
                        else
                            "Você precisa de pelo menos 3 treinos para ver estatísticas detalhadas.",
                        style = CoreoType.Body,
                        color = CoreoColors.TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(horizontal = CoreoSpacing.L, vertical = CoreoSpacing.M),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Last7DaysChart(data = repo.last7DaysChartData(sessions)) }
                item { ConsistencyCalendar(data = repo.last12WeeksCalendarData(sessions, settings)) }
                item { InsightCards(repo = repo, sessions = sessions) }
            }
        }
    }
}

// MARK: - Last 7 Days Chart

@Composable
private fun Last7DaysChart(data: List<DayData>) {
    val maxVal = data.maxOfOrNull { it.totalSeconds }?.takeIf { it > 0 } ?: 1

    StatSection(title = "Últimos 7 Dias", icon = Icons.Default.BarChart, iconColor = CoreoColors.Accent) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .height(160.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom
        ) {
            data.forEach { day ->
                val fraction  = day.totalSeconds.toFloat() / maxVal.toFloat()
                val barHeight = (120 * fraction).coerceAtLeast(if (day.totalSeconds > 0) 4f else 0f)
                // Format day label from LocalDate
                val label = day.date.dayOfWeek.name.take(3)
                    .lowercase().replaceFirstChar { it.uppercase() }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier            = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 3.dp)
                            .height(barHeight.dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                if (day.totalSeconds > 0) CoreoColors.Accent
                                else CoreoColors.PrimaryLight
                            )
                    )
                    Text(
                        text     = label,
                        style    = CoreoType.Small,
                        color    = CoreoColors.TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

// MARK: - Consistency Calendar

@Composable
private fun ConsistencyCalendar(data: List<CalendarDay>) {
    StatSection(
        title     = "Consistência (12 semanas)",
        icon      = Icons.Default.CalendarMonth,
        iconColor = CoreoColors.Success
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            data.chunked(7).forEach { week ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    week.forEach { day ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(calendarColor(day.count, day.dayType == DayType.REST))
                        )
                    }
                }
            }

            // Legend
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Menos", style = CoreoType.Small, color = CoreoColors.TextSecondary)
                    listOf(0, 1, 2, 3).forEach { level ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(legendColor(level))
                        )
                    }
                    Text("Mais", style = CoreoType.Small, color = CoreoColors.TextSecondary)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(CoreoColors.TextSecondary.copy(alpha = 0.18f))
                    )
                    Text("Descanso", style = CoreoType.Small, color = CoreoColors.TextSecondary)
                }
            }
        }
    }
}

// MARK: - Insight Cards

@Composable
private fun InsightCards(repo: WorkoutRepository, sessions: List<WorkoutSession>) {
    val personalRecord = repo.personalRecord(sessions)
    val totalSets      = sessions.filter { it.isSet }.sumOf { it.completedReps }
    val avgTime        = repo.averageTime(sessions)
    val bestDay        = repo.bestDayOfWeek(sessions)   // Pair<String, Int>
    val biggestSet     = repo.biggestSet(sessions)       // WorkoutSession?

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmallInsightCard(
                modifier  = Modifier.weight(1f),
                icon      = Icons.Default.EmojiEvents,
                iconColor = CoreoColors.Accent,
                title     = "Recorde",
                value     = "${personalRecord}s",
                subtitle  = "Maior tempo"
            )
            SmallInsightCard(
                modifier  = Modifier.weight(1f),
                icon      = Icons.Default.Repeat,
                iconColor = CoreoColors.Success,
                title     = "Total de Series",
                value     = "$totalSets",
                subtitle  = "Sets completados"
            )
        }
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmallInsightCard(
                modifier  = Modifier.weight(1f),
                icon      = Icons.Default.Star,
                iconColor = CoreoColors.Primary,
                title     = "Melhor Dia",
                value     = bestDay.first,
                subtitle  = "${bestDay.second} treinos"
            )
            SmallInsightCard(
                modifier  = Modifier.weight(1f),
                icon      = Icons.Default.TrendingUp,
                iconColor = CoreoColors.Accent,
                title     = "Media",
                value     = "${avgTime}s",
                subtitle  = "Por treino"
            )
        }

        biggestSet?.let { set ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CoreoRadius.L))
                    .background(CoreoColors.Accent.copy(alpha = 0.08f))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector        = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint               = CoreoColors.Accent,
                            modifier           = Modifier.size(24.dp)
                        )
                        Text(
                            text       = "Maior Set Completado",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = CoreoColors.Text
                        )
                    }
                    Text(
                        text       = "${set.completedReps}x series",
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color      = CoreoColors.Accent
                    )
                    Text(
                        text  = "Tempo total: ${formatTime(set.totalExerciseTime)}",
                        style = CoreoType.Body,
                        color = CoreoColors.Text
                    )
                    Text(
                        text  = set.durations.joinToString(" • ") { "${it}s" },
                        style = CoreoType.Caption,
                        color = CoreoColors.TextSecondary
                    )
                }
            }
        }
    }
}

// MARK: - Reusable Components

@Composable
private fun StatSection(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CoreoRadius.L))
            .background(CoreoColors.PrimaryLight)
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = iconColor,
                    modifier           = Modifier.size(20.dp)
                )
                Text(
                    text       = title,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = CoreoColors.Text
                )
            }
            content()
        }
    }
}

@Composable
private fun SmallInsightCard(
    modifier: Modifier,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    subtitle: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(CoreoRadius.M))
            .background(CoreoColors.PrimaryLight)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = iconColor,
                modifier           = Modifier.size(20.dp)
            )
            Text(
                text       = value,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = CoreoColors.Text
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text       = title,
                    style      = CoreoType.Caption,
                    fontWeight = FontWeight.SemiBold,
                    color      = CoreoColors.Text
                )
                Text(
                    text  = subtitle,
                    style = CoreoType.Small,
                    color = CoreoColors.TextSecondary
                )
            }
        }
    }
}

// MARK: - Helpers

private fun calendarColor(count: Int, isRestDay: Boolean): Color {
    if (count > 0) return when (count) {
        1    -> CoreoColors.Success.copy(alpha = 0.4f)
        2    -> CoreoColors.Success.copy(alpha = 0.6f)
        else -> CoreoColors.Success
    }
    return if (isRestDay) CoreoColors.TextSecondary.copy(alpha = 0.18f)
    else CoreoColors.PrimaryLight.copy(alpha = 0.3f)
}

private fun legendColor(level: Int): Color = when (level) {
    0    -> CoreoColors.PrimaryLight.copy(alpha = 0.3f)
    1    -> CoreoColors.Success.copy(alpha = 0.4f)
    2    -> CoreoColors.Success.copy(alpha = 0.6f)
    else -> CoreoColors.Success
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return if (m > 0) "${m}m ${s}s" else "${s}s"
}
