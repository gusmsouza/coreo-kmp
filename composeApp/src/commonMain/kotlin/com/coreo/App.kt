package com.coreo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.coreo.theme.CoreoColors
import com.coreo.theme.CoreoConstants
import com.coreo.theme.CoreoTheme
import com.coreo.theme.CoreoType

@Composable
fun App() {
    CoreoTheme {
        // Placeholder -- will be replaced with MainScreen in Phase 5
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CoreoColors.Background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text    = CoreoConstants.AppName,
                style   = CoreoType.Headline,
                color   = CoreoColors.Primary
            )
        }
    }
}