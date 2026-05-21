package com.dskochilov.easydinner.domain.model

data class CookingPreferences(
    val maxReadyTimeMinutes: Int,
    val servings: Int,
    val useOven: Boolean,
)
