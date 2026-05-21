package com.dskochilov.easydinner.domain.model

data class RecipeSearchResult(
    val recipes: List<Recipe>,
    val source: RecipeSource,
)

enum class RecipeSource {
    Mock,
    Spoonacular,
}
