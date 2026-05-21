package com.dskochilov.easydinner.shared

import kotlinx.serialization.Serializable

@Serializable
data class RecipeSearchRequestDto(
    val ingredients: List<String>,
    val preferences: CookingPreferencesDto = CookingPreferencesDto(),
)
