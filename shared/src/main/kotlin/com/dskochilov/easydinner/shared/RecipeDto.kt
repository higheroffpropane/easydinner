package com.dskochilov.easydinner.shared

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(
    val id: Int,
    val title: String,
    val imageUrl: String? = null,
    val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val usedIngredients: List<String> = emptyList(),
    val missedIngredients: List<String> = emptyList(),
    val allIngredients: List<String> = emptyList(),
    val summary: String? = null,
    val instructions: String? = null,
    val dishTypes: List<String> = emptyList(),
    val diets: List<String> = emptyList(),
    val sourceUrl: String? = null,
)
