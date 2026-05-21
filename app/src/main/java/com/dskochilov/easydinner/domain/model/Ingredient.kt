package com.dskochilov.easydinner.domain.model

data class Ingredient(
    val name: String,
    val source: IngredientSource,
)

enum class IngredientSource {
    UserText,
    PhotoAi,
    ManualEdit,
}
