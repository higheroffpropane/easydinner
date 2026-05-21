package com.dskochilov.easydinner.shared

import kotlinx.serialization.Serializable

@Serializable
data class CookingPreferencesDto(
    val maxReadyTimeMinutes: Int? = null,
    val servings: Int? = null,
    val useOven: Boolean? = null,
)
