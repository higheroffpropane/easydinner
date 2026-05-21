package com.dskochilov.easydinner.server.recipe

import com.dskochilov.easydinner.shared.RecipeSearchRequestDto
import com.dskochilov.easydinner.shared.RecipeSearchResponseDto

interface RecipeProvider {
    suspend fun search(request: RecipeSearchRequestDto): RecipeSearchResponseDto
}
