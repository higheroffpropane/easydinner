package com.dskochilov.easydinner.server.recipe

import com.dskochilov.easydinner.shared.RecipeDto
import com.dskochilov.easydinner.shared.RecipeSearchRequestDto
import com.dskochilov.easydinner.shared.RecipeSearchResponseDto
import com.dskochilov.easydinner.shared.RecipeSourceDto

class MockRecipeProvider : RecipeProvider {
    override suspend fun search(request: RecipeSearchRequestDto): RecipeSearchResponseDto {
        val ingredients = request.ingredients.map { it.trim() }.filter { it.isNotBlank() }
        val main = ingredients.take(3).joinToString(", ")
        val maxTime = request.preferences.maxReadyTimeMinutes ?: 35
        val servings = request.preferences.servings ?: 2

        return RecipeSearchResponseDto(
            source = RecipeSourceDto.Mock,
            recipes = listOf(
                RecipeDto(
                    id = 1,
                    title = "Quick skillet dinner with $main",
                    readyInMinutes = maxTime.coerceAtMost(35),
                    servings = servings,
                    usedIngredients = ingredients.take(5),
                    missedIngredients = listOf("olive oil", "salt", "pepper"),
                    allIngredients = ingredients.take(5) + listOf("1 tbsp olive oil", "salt", "pepper"),
                    summary = "A fast skillet dinner built around the ingredients you already have.",
                    instructions = "Heat a pan, add olive oil, cook the main ingredients until tender, then season to taste.",
                ),
                RecipeDto(
                    id = 2,
                    title = "Simple bowl from fridge ingredients",
                    readyInMinutes = maxTime.coerceAtMost(25),
                    servings = servings,
                    usedIngredients = ingredients.take(4),
                    missedIngredients = listOf("fresh herbs"),
                    allIngredients = ingredients.take(4) + listOf("fresh herbs"),
                    summary = "A simple bowl that keeps prep low and uses fridge ingredients efficiently.",
                    instructions = "Cook or warm the ingredients, combine in a bowl, and finish with fresh herbs.",
                ),
                RecipeDto(
                    id = 3,
                    title = if (request.preferences.useOven == true) {
                        "Oven bake with $main"
                    } else {
                        "No-oven stovetop meal"
                    },
                    readyInMinutes = maxTime,
                    servings = servings,
                    usedIngredients = ingredients.take(6),
                    missedIngredients = listOf("garlic"),
                    allIngredients = ingredients.take(6) + listOf("garlic"),
                    summary = "A flexible dinner idea that follows the current oven preference.",
                    instructions = if (request.preferences.useOven == true) {
                        "Combine ingredients in a baking dish and bake until hot and lightly browned."
                    } else {
                        "Cook ingredients on the stovetop in a covered pan until everything is tender."
                    },
                ),
            ),
        )
    }
}
