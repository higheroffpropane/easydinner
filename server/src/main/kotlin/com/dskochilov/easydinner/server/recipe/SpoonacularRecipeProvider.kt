package com.dskochilov.easydinner.server.recipe

import com.dskochilov.easydinner.shared.RecipeDto
import com.dskochilov.easydinner.shared.RecipeSearchRequestDto
import com.dskochilov.easydinner.shared.RecipeSearchResponseDto
import com.dskochilov.easydinner.shared.RecipeSourceDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SpoonacularRecipeProvider(
    private val apiKey: String,
    private val client: HttpClient,
) : RecipeProvider {

    override suspend fun search(request: RecipeSearchRequestDto): RecipeSearchResponseDto {
        val response = client.get("https://api.spoonacular.com/recipes/complexSearch") {
            parameter("apiKey", apiKey)
            parameter("includeIngredients", request.ingredients.joinToString(","))
            parameter("number", 10)
            parameter("addRecipeInformation", true)
            parameter("fillIngredients", true)
            request.preferences.maxReadyTimeMinutes?.let { parameter("maxReadyTime", it) }
            request.preferences.servings?.let { parameter("servings", it) }
            if (request.preferences.useOven == true) {
                parameter("equipment", "oven")
            }
        }.body<SpoonacularSearchResponse>()

        return RecipeSearchResponseDto(
            source = RecipeSourceDto.Spoonacular,
            recipes = response.results.map { recipe ->
                RecipeDto(
                    id = recipe.id,
                    title = recipe.title,
                    imageUrl = recipe.image,
                    readyInMinutes = recipe.readyInMinutes,
                    servings = recipe.servings,
                    usedIngredients = recipe.usedIngredients.mapNotNull { it.name },
                    missedIngredients = recipe.missedIngredients.mapNotNull { it.name },
                    allIngredients = recipe.extendedIngredients.mapNotNull { it.original ?: it.name },
                    summary = recipe.summary,
                    instructions = recipe.instructions,
                    dishTypes = recipe.dishTypes,
                    diets = recipe.diets,
                    sourceUrl = recipe.sourceUrl,
                )
            },
        )
    }
}

@Serializable
private data class SpoonacularSearchResponse(
    val results: List<SpoonacularRecipe> = emptyList(),
)

@Serializable
private data class SpoonacularRecipe(
    val id: Int,
    val title: String,
    val image: String? = null,
    val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val sourceUrl: String? = null,
    val summary: String? = null,
    val instructions: String? = null,
    val dishTypes: List<String> = emptyList(),
    val diets: List<String> = emptyList(),
    val extendedIngredients: List<SpoonacularIngredient> = emptyList(),
    @SerialName("usedIngredients")
    val usedIngredients: List<SpoonacularIngredient> = emptyList(),
    @SerialName("missedIngredients")
    val missedIngredients: List<SpoonacularIngredient> = emptyList(),
)

@Serializable
private data class SpoonacularIngredient(
    val name: String? = null,
    val original: String? = null,
)
