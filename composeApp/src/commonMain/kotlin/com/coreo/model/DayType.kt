package com.coreo.model

enum class DayType {
    PLANK,
    REST,
    INTENSITY;

    val displayName: String get() = when (this) {
        PLANK     -> "Prancha"
        REST      -> "Descanso"
        INTENSITY -> "Intensidade"
    }

    val icon: String get() = when (this) {
        PLANK     -> "figure.strengthtraining.traditional"
        REST      -> "moon.fill"
        INTENSITY -> "bolt.fill"
    }

    fun next(intensityAllowed: Boolean): DayType = when (this) {
        PLANK     -> REST
        REST      -> if (intensityAllowed) INTENSITY else PLANK
        INTENSITY -> PLANK
    }
}