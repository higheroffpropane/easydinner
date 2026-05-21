package com.dskochilov.easydinner.shared

import kotlinx.serialization.Serializable

@Serializable
enum class RecipeSourceDto {
    Mock,
    Spoonacular,
}
