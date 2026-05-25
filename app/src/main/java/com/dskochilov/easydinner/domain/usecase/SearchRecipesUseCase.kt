package com.dskochilov.easydinner.domain.usecase

import com.dskochilov.easydinner.domain.model.CookingPreferences
import com.dskochilov.easydinner.domain.model.Ingredient
import com.dskochilov.easydinner.domain.model.IngredientSource
import com.dskochilov.easydinner.domain.model.RecipeSearchResult
import com.dskochilov.easydinner.domain.repository.RecipeRepository
import kotlinx.coroutines.CancellationException

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
            return Result.failure(IllegalArgumentException("At least one ingredient is required"))
        }

        return try {
            Result.success(repository.searchRecipes(ingredients, preferences))
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }
}
