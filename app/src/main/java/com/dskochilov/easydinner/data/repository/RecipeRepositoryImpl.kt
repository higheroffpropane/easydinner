package com.dskochilov.easydinner.data.repository

import com.dskochilov.easydinner.data.remote.EasyDinnerApi
import com.dskochilov.easydinner.domain.model.CookingPreferences
import com.dskochilov.easydinner.domain.model.Ingredient
import com.dskochilov.easydinner.domain.model.Recipe
import com.dskochilov.easydinner.domain.model.RecipeSearchResult
import com.dskochilov.easydinner.domain.model.RecipeSource
import com.dskochilov.easydinner.domain.repository.RecipeRepository
import com.dskochilov.easydinner.shared.CookingPreferencesDto
import com.dskochilov.easydinner.shared.RecipeDto
import com.dskochilov.easydinner.shared.RecipeSearchRequestDto
import com.dskochilov.easydinner.shared.RecipeSourceDto

class RecipeRepositoryImpl(
    private val api: EasyDinnerApi,
) : RecipeRepository {
    override suspend fun searchRecipes(
        ingredients: List<Ingredient>,
        preferences: CookingPreferences,
    ): RecipeSearchResult {
        val response = api.searchRecipes(
            RecipeSearchRequestDto(
                ingredients = ingredients.map { it.name },
                preferences = CookingPreferencesDto(
                    maxReadyTimeMinutes = preferences.maxReadyTimeMinutes,
                    servings = preferences.servings,
                    useOven = preferences.useOven,
                ),
            ),
        )

        return RecipeSearchResult(
            source = response.source.toDomain(),
            recipes = response.recipes.map { it.toDomain() },
        )
    }

    private fun RecipeSourceDto.toDomain(): RecipeSource = when (this) {
        RecipeSourceDto.Mock -> RecipeSource.Mock
        RecipeSourceDto.Spoonacular -> RecipeSource.Spoonacular
    }

    private fun RecipeDto.toDomain(): Recipe = Recipe(
        id = id,
        title = title,
        imageUrl = imageUrl,
        readyInMinutes = readyInMinutes,
        servings = servings,
        usedIngredients = usedIngredients,
        missedIngredients = missedIngredients,
        allIngredients = allIngredients,
        summary = summary,
        instructions = instructions,
        dishTypes = dishTypes,
        diets = diets,
        sourceUrl = sourceUrl,
    )
}
