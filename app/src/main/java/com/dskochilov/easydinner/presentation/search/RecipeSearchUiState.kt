package com.dskochilov.easydinner.presentation.search

import com.dskochilov.easydinner.domain.model.Recipe
import com.dskochilov.easydinner.domain.model.RecipeSource

data class RecipeSearchUiState(
    val ingredientText: String = "",
    val maxReadyTimeMinutes: Int = DefaultMaxReadyTimeMinutes,
    val servings: Int = DefaultServings,
    val useOven: Boolean = DefaultUseOven,
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val source: RecipeSource? = null,
    val errorMessage: String? = null,
) {
    companion object {
        const val DefaultMaxReadyTimeMinutes = 30
        const val DefaultServings = 2
        const val DefaultUseOven = false
    }
}
