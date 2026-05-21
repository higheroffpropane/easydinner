package com.dskochilov.easydinner.domain.usecase

import com.dskochilov.easydinner.domain.model.CookingPreferences
import com.dskochilov.easydinner.domain.model.Ingredient
import com.dskochilov.easydinner.domain.model.IngredientSource
import com.dskochilov.easydinner.domain.model.RecipeSearchResult
import com.dskochilov.easydinner.domain.repository.RecipeRepository

class SearchRecipesUseCase(
    private val repository: RecipeRepository,
) {
    suspend operator fun invoke(
        ingredientText: String,
        preferences: CookingPreferences,
    ): Result<RecipeSearchResult> {
        val ingredients = ingredientText
            .split(',', ';', '\n')
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .map { Ingredient(name = it, source = IngredientSource.UserText) }

        if (ingredients.isEmpty()) {
            return Result.failure(IllegalArgumentException("Добавь хотя бы один ингредиент"))
        }

        return runCatching {
            repository.searchRecipes(ingredients, preferences)
        }
    }
}
