package com.dskochilov.easydinner.data.remote

import com.dskochilov.easydinner.shared.RecipeSearchRequestDto
import com.dskochilov.easydinner.shared.RecipeSearchResponseDto

interface EasyDinnerApi {
    suspend fun searchRecipes(request: RecipeSearchRequestDto): RecipeSearchResponseDto
}
