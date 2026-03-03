package com.coreo.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coreo.model.Goal
import com.coreo.model.UserSettings
import com.coreo.model.WorkoutSession
import com.coreo.repository.WorkoutRepository
import com.coreo.theme.CoreoColors
import com.coreo.theme.CoreoConstants
import com.coreo.theme.CoreoRadius
import com.coreo.theme.CoreoSpacing
import com.coreo.theme.CoreoType
import com.coreo.ui.components.CoreoLogo
import com.coreo.ui.components.StatCard

@Composable
fun MainScreen(
    sessions: List<WorkoutSession>,
    goals: List<Goal>,
    settings: UserSettings,
    onStartWorkout: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenGoalSetup: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val repo        = WorkoutRepository()
    val streak      = repo.calculateStreak(sessions, settings)
    val activeGoal  = repo.activeGoal(goals)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CoreoColors.Background)
    ) {
        if (sessions.isEmpty()) {
            EmptyStateContent(
                onStartWorkout = onStartWorkout,
                onOpenStats    = onOpenStats,
                onOpenHistory  = onOpenHistory
            )
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding      = androidx.compose.foundation.layout.PaddingValues(
                    start  = CoreoSpacing.L,
                    end    = CoreoSpacing.L,
                    top    = 16.dp,
                    bottom = 24.dp
                )
            ) {
                // Header
                item { HeaderRow(onOpenSettings = onOpenSettings) }

                // Goal card or prompt
                item {
                    if (activeGoal != null) {
                        GoalCard(goal = activeGoal, onClick = onOpenGoalSetup)
                    } else {
                        CreateGoalPrompt(onClick = onOpenGoalSetup)
                    }
                }

                // Streak card
                if (streak > 0) {
                    item { StreakCard(streak = streak) }
                }

                // 2x2 Stats grid
                item { StatsGrid(sessions = sessions, repo = repo) }

                // Workouts this week
                item { WorkoutsThisWeekCard(count = repo.workoutsThisWeek(sessions)) }

                // CTA + nav buttons
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(CoreoSpacing.M)) {
                        Button(
                            onClick  = onStartWorkout,
                            modifier = Modifier.fillMaxWidth(),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor = CoreoColors.Accent
                            ),
                            shape = RoundedCornerShape(CoreoRadius.L)
                        ) {
                            Text(
                                text       = "Iniciar Prancha",
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = CoreoColors.Background,
                                modifier   = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        Row(
                            modifier            = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment   = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = onOpenStats) {
                                Icon(
                                    imageVector        = Icons.Default.BarChart,
                                    contentDescription = null,
                                    tint               = CoreoColors.Primary,
                                    modifier           = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text     = "Estatísticas",
                                    color    = CoreoColors.Primary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text  = "•",
                                color = CoreoColors.TextSecondary,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            TextButton(onClick = onOpenHistory) {
                                Icon(
                                    imageVector        = Icons.Default.History,
                                    contentDescription = null,
                                    tint               = CoreoColors.Primary,
                                    modifier           = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text     = "Histórico",
                                    color    = CoreoColors.Primary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// MARK: - Header

@Composable
private fun HeaderRow(onOpenSettings: () -> Unit) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment  = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CoreoLogo(size = 40.dp)
            Column {
                Text(
                    text       = CoreoConstants.AppName,
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = CoreoColors.Primary
                )
                Text(
                    text  = CoreoConstants.Tagline,
                    style = CoreoType.Small,
                    color = CoreoColors.TextSecondary
                )
            }
        }
        IconButton(onClick = onOpenSettings) {
            Icon(
                imageVector        = Icons.Default.Settings,
                contentDescription = "Configuracoes",
                tint               = CoreoColors.Primary
            )
        }
    }
}

// MARK: - Streak Card

@Composable
private fun StreakCard(streak: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CoreoRadius.XL))
            .background(CoreoColors.PrimaryLight)
            .border(1.dp, CoreoColors.Success.copy(alpha = 0.3f), RoundedCornerShape(CoreoRadius.XL))
            .padding(CoreoSpacing.L),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint               = CoreoColors.Success,
                modifier           = Modifier.size(24.dp)
            )
            Text(
                text  = "Sequência Atual",
                style = CoreoType.H3,
                color = CoreoColors.Text
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text       = "$streak",
                fontSize   = 56.sp,
                fontWeight = FontWeight.Bold,
                color      = CoreoColors.Success
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text       = if (streak == 1) "dia" else "dias",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Medium,
                color      = CoreoColors.TextSecondary,
                modifier   = Modifier.padding(bottom = 10.dp)
            )
        }
    }
}

// MARK: - Stats Grid

@Composable
private fun StatsGrid(sessions: List<WorkoutSession>, repo: WorkoutRepository) {
    val record  = repo.personalRecord(sessions)
    val total   = repo.totalTimeAllTime(sessions)
    val last7   = repo.last7DaysTotal(sessions)
    val evoPct  = repo.evolutionPercentage(sessions)

    val totalM  = total / 60; val totalS = total % 60
    val last7M  = last7 / 60; val last7S = last7 % 60

    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
        columns             = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
        modifier            = Modifier.height(252.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled   = false
    ) {
        item {
            StatCard(
                title     = "Recorde",
                value     = "${record}s",
                icon      = Icons.Default.EmojiEvents,
                iconColor = CoreoColors.Accent
            )
        }
        item {
            StatCard(
                title    = "Total Geral",
                value    = if (totalM > 0) "${totalM}m ${totalS}s" else "${totalS}s",
                icon     = Icons.Default.Schedule,
                iconColor = CoreoColors.Primary,
                subtitle = "${sessions.size} treinos"
            )
        }
        item {
            StatCard(
                title     = "Últimos 7 Dias",
                value     = if (last7M > 0) "${last7M}m ${last7S}s" else "${last7S}s",
                icon      = Icons.Default.Star,
                iconColor = CoreoColors.Success
            )
        }
        item { EvolutionCard(evoPct = evoPct) }
    }
}

// MARK: - Evolution Card

@Composable
private fun EvolutionCard(evoPct: Int?) {
    val isPositive = (evoPct ?: 0) >= 0
    val color      = if (isPositive) CoreoColors.Success else CoreoColors.Error

    Column(
        modifier = Modifier
            .height(120.dp)
            .clip(RoundedCornerShape(CoreoRadius.L))
            .background(CoreoColors.PrimaryLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.TrendingUp,
                contentDescription = null,
                tint               = color,
                modifier           = Modifier.size(14.dp)
            )
            Text(
                text  = "Evolução",
                style = CoreoType.Caption,
                color = CoreoColors.TextSecondary
            )
        }

        if (evoPct != null) {
            Column {
                Text(
                    text       = if (evoPct >= 0) "+$evoPct%" else "$evoPct%",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = color
                )
                Text(
                    text  = "vs semana anterior",
                    style = CoreoType.Small,
                    color = CoreoColors.TextSecondary
                )
            }
        } else {
            Column {
                Text(
                    text       = "--",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = CoreoColors.TextSecondary
                )
                Text(
                    text  = "Sem dados anteriores",
                    style = CoreoType.Small,
                    color = CoreoColors.TextSecondary
                )
            }
        }
    }
}

// MARK: - Workouts This Week Card

@Composable
private fun WorkoutsThisWeekCard(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CoreoRadius.L))
            .background(CoreoColors.PrimaryLight)
            .padding(CoreoSpacing.L),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector        = Icons.Default.CheckCircle,
            contentDescription = null,
            tint               = CoreoColors.Primary,
            modifier           = Modifier.size(32.dp)
        )
        Column {
            Text(
                text       = "$count treinos",
                style      = CoreoType.H2,
                color      = CoreoColors.Text,
                fontWeight = FontWeight.Bold
            )
            Text(
                text  = "esta semana",
                style = CoreoType.Body,
                color = CoreoColors.TextSecondary
            )
        }
    }
}

// MARK: - Goal Card

@Composable
private fun GoalCard(goal: Goal, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CoreoRadius.XL))
            .background(CoreoColors.PrimaryLight)
            .border(1.dp, CoreoColors.Accent.copy(alpha = 0.3f), RoundedCornerShape(CoreoRadius.XL))
            .clickable(onClick = onClick)
            .padding(CoreoSpacing.L),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(text = "Meta Atual", style = CoreoType.H3, color = CoreoColors.Text)
            Text(text = ">", color = CoreoColors.TextSecondary)
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CoreoColors.PrimaryLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (goal.progressPercentage / 100).toFloat())
                    .height(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CoreoColors.Accent)
            )
        }

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text       = "${goal.completedWorkouts}/${goal.targetWorkouts} treinos",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = CoreoColors.Text
                )
                Text(
                    text  = if (goal.daysRemaining > 0) "Faltam ${goal.daysRemaining} dias" else "Meta expirada",
                    style = CoreoType.Caption,
                    color = CoreoColors.TextSecondary
                )
            }
            Text(
                text     = goal.status.displayText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color    = CoreoColors.Background,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CoreoColors.Accent)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

// MARK: - Create Goal Prompt

@Composable
private fun CreateGoalPrompt(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CoreoRadius.XL))
            .background(CoreoColors.PrimaryLight)
            .border(1.dp, CoreoColors.Primary.copy(alpha = 0.2f), RoundedCornerShape(CoreoRadius.XL))
            .clickable(onClick = onClick)
            .padding(CoreoSpacing.L),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector        = Icons.Default.Add,
            contentDescription = null,
            tint               = CoreoColors.Accent,
            modifier           = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Criar Meta",    style = CoreoType.H3,    color = CoreoColors.Text)
            Text(text = "Defina um objetivo para manter a consistência",
                style = CoreoType.Caption, color = CoreoColors.TextSecondary)
        }
        Icon(
            imageVector        = Icons.Default.Add,
            contentDescription = null,
            tint               = CoreoColors.Primary,
            modifier           = Modifier.size(24.dp)
        )
    }
}

// MARK: - Empty State

@Composable
private fun EmptyStateContent(
    onStartWorkout: () -> Unit,
    onOpenStats:    () -> Unit,
    onOpenHistory:  () -> Unit
) {
    Column(
        modifier              = Modifier
            .fillMaxSize()
            .padding(CoreoSpacing.L),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center
    ) {
        CoreoLogo(size = 100.dp)

        Spacer(Modifier.height(32.dp))

        Text(
            text       = "Sua Jornada Começa Aqui",
            fontSize   = 24.sp,
            fontWeight = FontWeight.Bold,
            color      = CoreoColors.Text
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text  = "Faça sua primeira prancha e comece a construir consistência",
            style = CoreoType.Body,
            color = CoreoColors.TextSecondary,
            modifier = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick  = onStartWorkout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CoreoColors.Accent),
            shape  = RoundedCornerShape(16.dp)
        ) {
            Text(
                text       = "Começar Agora",
                fontSize   = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color      = CoreoColors.Background,
                modifier   = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onOpenStats) {
                Text(text = "Estatísticas", color = CoreoColors.Primary, fontWeight = FontWeight.Medium)
            }
            Text(text = "•", color = CoreoColors.TextSecondary, modifier = Modifier.padding(horizontal = 4.dp))
            TextButton(onClick = onOpenHistory) {
                Text(text = "Histórico", color = CoreoColors.Primary, fontWeight = FontWeight.Medium)
            }
        }
    }
}