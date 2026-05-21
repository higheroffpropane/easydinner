package com.dskochilov.easydinner.shared

import kotlinx.serialization.Serializable

@Serializable
data class RecipeSearchResponseDto(
    val recipes: List<RecipeDto>,
    val source: RecipeSourceDto,
)
