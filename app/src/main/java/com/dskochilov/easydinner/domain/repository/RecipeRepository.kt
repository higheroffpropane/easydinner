package com.dskochilov.easydinner.domain.repository

import com.dskochilov.easydinner.domain.model.CookingPreferences
import com.dskochilov.easydinner.domain.model.Ingredient
import com.dskochilov.easydinner.domain.model.RecipeSearchResult

interface RecipeRepository {
    suspend fun searchRecipes(
        ingredients: List<Ingredient>,
        preferences: CookingPreferences,
    ): RecipeSearchResult
}
