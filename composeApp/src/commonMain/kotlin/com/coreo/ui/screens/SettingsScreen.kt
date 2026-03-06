package com.coreo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coreo.model.DayType
import com.coreo.model.UserSettings
import com.coreo.theme.CoreoColors
import com.coreo.theme.CoreoRadius
import com.coreo.theme.CoreoSpacing
import com.coreo.theme.CoreoType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: UserSettings,
    onSettingsChanged: (UserSettings) -> Unit,
    onResetAllData: () -> Unit,
    onBack: () -> Unit
) {
    var showResetConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CoreoColors.Background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text       = "Configurações",
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

        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(horizontal = CoreoSpacing.L, vertical = CoreoSpacing.M),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Workout Settings
            item {
                SettingsSection(title = "Configurações de Treino", icon = Icons.Default.FitnessCenter) {
                    StepperRow(
                        title       = "Duração Padrão",
                        subtitle    = "Para treinos simples",
                        value       = settings.defaultDuration,
                        min         = 10,
                        max         = 300,
                        step        = 5,
                        display     = "${settings.defaultDuration}s",
                        onDecrement = { onSettingsChanged(settings.copy(defaultDuration = settings.defaultDuration - 5)) },
                        onIncrement = { onSettingsChanged(settings.copy(defaultDuration = settings.defaultDuration + 5)) }
                    )
                    HorizontalDivider(
                        color    = CoreoColors.PrimaryLight,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    StepperRow(
                        title       = "Intervalo Padrão",
                        subtitle    = "Entre séries em sets",
                        value       = settings.defaultRestDuration,
                        min         = 15,
                        max         = 180,
                        step        = 5,
                        display     = "${settings.defaultRestDuration}s",
                        onDecrement = { onSettingsChanged(settings.copy(defaultRestDuration = settings.defaultRestDuration - 5)) },
                        onIncrement = { onSettingsChanged(settings.copy(defaultRestDuration = settings.defaultRestDuration + 5)) }
                    )
                    HorizontalDivider(
                        color    = CoreoColors.PrimaryLight,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    ToggleRow(
                        title           = "Mostrar Countdown",
                        subtitle        = "Contagem 3, 2, 1 antes de começar",
                        checked         = settings.alwaysShowCountdown,
                        onCheckedChange = { onSettingsChanged(settings.copy(alwaysShowCountdown = it)) }
                    )
                }
            }

            // Weekly Schedule
            item {
                SettingsSection(title = "Planejamento Semanal", icon = Icons.Default.CalendarMonth) {
                    val weekdays = listOf(
                        2 to "Segunda", 3 to "Terça",  4 to "Quarta",
                        5 to "Quinta",  6 to "Sexta",  7 to "Sábado", 1 to "Domingo"
                    )
                    weekdays.forEachIndexed { index, (weekday, name) ->
                        if (index > 0) {
                            HorizontalDivider(
                                color    = CoreoColors.PrimaryLight,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                        DayTypeRow(
                            dayName = name,
                            dayType = settings.weekSchedule[weekday] ?: DayType.PLANK,
                            onClick = {
                                val current         = settings.weekSchedule[weekday] ?: DayType.PLANK
                                val hasIntensity    = settings.weekSchedule.values.contains(DayType.INTENSITY)
                                val canAddIntensity = !hasIntensity || current == DayType.INTENSITY
                                val next = when (current) {
                                    DayType.PLANK     -> DayType.REST
                                    DayType.REST      -> if (canAddIntensity) DayType.INTENSITY else DayType.PLANK
                                    DayType.INTENSITY -> DayType.PLANK
                                }
                                val newSchedule = settings.weekSchedule.toMutableMap()
                                newSchedule[weekday] = next
                                onSettingsChanged(settings.copy(weekSchedule = newSchedule))
                            }
                        )
                    }
                    Column(
                        modifier            = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text  = "🌙 Dias de descanso não quebram sua sequência.",
                            style = CoreoType.Small,
                            color = CoreoColors.TextSecondary
                        )
                        Text(
                            text  = "⚡ Máximo de 1 dia de intensidade por semana.",
                            style = CoreoType.Small,
                            color = CoreoColors.TextSecondary
                        )
                    }
                }
            }

            // Notifications
            item {
                SettingsSection(title = "Notificações", icon = Icons.Default.Notifications) {
                    ToggleRow(
                        title           = "Lembrete Diário",
                        subtitle        = "Mensagem adaptada ao tipo do dia",
                        checked         = settings.notificationsEnabled,
                        onCheckedChange = { onSettingsChanged(settings.copy(notificationsEnabled = it)) }
                    )
                }
            }

            // Data
            item {
                SettingsSection(title = "Dados", icon = Icons.Default.Delete) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetConfirmation = true }
                            .padding(vertical = 16.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Delete,
                            contentDescription = null,
                            tint               = CoreoColors.Error,
                            modifier           = Modifier.size(20.dp)
                        )
                        Text(
                            text       = "Apagar Todos os Dados",
                            fontSize   = 16.sp,
                            color      = CoreoColors.Error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text     = "Esta ação apagará permanentemente todos os treinos, metas e configurações.",
                        style    = CoreoType.Small,
                        color    = CoreoColors.TextSecondary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                }
            }

            // About
            item {
                SettingsSection(title = "Sobre", icon = Icons.Default.Info) {
                    AboutRow(label = "Versão", value = "1.0.0")
                    HorizontalDivider(
                        color    = CoreoColors.PrimaryLight,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    AboutRow(label = "Desenvolvido com", value = "♥ para wellness")
                }
            }
        }
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title   = { Text("Apagar Todos os Dados") },
            text    = { Text("Esta ação é irreversível. Todos os seus treinos, metas e configurações serão permanentemente apagados.") },
            confirmButton = {
                TextButton(onClick = { showResetConfirmation = false; onResetAllData() }) {
                    Text("Apagar", color = CoreoColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text("Cancelar", color = CoreoColors.Primary)
                }
            }
        )
    }
}

// MARK: - Section Wrapper
// FIX: was Box (stacks children) → now Column (arranges vertically)

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = CoreoColors.Primary,
                modifier           = Modifier.size(18.dp)
            )
            Text(
                text       = title,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = CoreoColors.Text
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CoreoRadius.L))
                .background(CoreoColors.PrimaryLight)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            content()
        }
    }
}

// MARK: - Stepper Row

@Composable
private fun StepperRow(
    title: String,
    subtitle: String,
    value: Int,
    min: Int,
    max: Int,
    step: Int,
    display: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = title,    fontSize = 16.sp, fontWeight = FontWeight.Medium, color = CoreoColors.Text)
            Text(text = subtitle, style    = CoreoType.Small, color = CoreoColors.TextSecondary)
        }
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier         = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (value > min) CoreoColors.Primary else CoreoColors.Primary.copy(alpha = 0.3f))
                    .clickable(enabled = value > min) { onDecrement() },
                verticalArrangement   = Arrangement.Center,
                horizontalAlignment   = Alignment.CenterHorizontally
            ) {
                Text(text = "−", fontSize = 20.sp, color = CoreoColors.Background, fontWeight = FontWeight.Bold)
            }
            Text(
                text       = display,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = CoreoColors.Accent,
                modifier   = Modifier.padding(horizontal = 4.dp)
            )
            Column(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (value < max) CoreoColors.Primary else CoreoColors.Primary.copy(alpha = 0.3f))
                    .clickable(enabled = value < max) { onIncrement() },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "+", fontSize = 20.sp, color = CoreoColors.Background, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// MARK: - Toggle Row

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = title,    fontSize = 16.sp, fontWeight = FontWeight.Medium, color = CoreoColors.Text)
            Text(text = subtitle, style    = CoreoType.Small, color = CoreoColors.TextSecondary)
        }
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors          = SwitchDefaults.colors(
                checkedThumbColor = CoreoColors.Background,
                checkedTrackColor = CoreoColors.Accent
            )
        )
    }
}

// MARK: - Day Type Row

@Composable
private fun DayTypeRow(
    dayName: String,
    dayType: DayType,
    onClick: () -> Unit
) {
    val (pillBg, pillText) = when (dayType) {
        DayType.PLANK     -> CoreoColors.Primary.copy(alpha = 0.12f) to CoreoColors.Primary
        DayType.REST      -> CoreoColors.TextSecondary.copy(alpha = 0.12f) to CoreoColors.TextSecondary
        DayType.INTENSITY -> CoreoColors.Accent.copy(alpha = 0.15f) to CoreoColors.Accent
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = dayName, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = CoreoColors.Text)
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(pillBg)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = dayType.displayName,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = pillText
            )
        }
    }
}

// MARK: - About Row

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = CoreoColors.Text)
        Text(text = value, fontSize = 16.sp, color = CoreoColors.TextSecondary)
    }
}
