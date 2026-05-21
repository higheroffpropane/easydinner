package com.dskochilov.easydinner.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val readyInMinutes: Int?,
    val servings: Int?,
    val usedIngredients: List<String>,
    val missedIngredients: List<String>,
    val allIngredients: List<String>,
    val summary: String?,
    val instructions: String?,
    val dishTypes: List<String>,
    val diets: List<String>,
    val sourceUrl: String?,
)
