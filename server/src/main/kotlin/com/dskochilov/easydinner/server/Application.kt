package com.dskochilov.easydinner.server

import com.dskochilov.easydinner.server.recipe.MockRecipeProvider
import com.dskochilov.easydinner.server.recipe.SpoonacularRecipeProvider
import com.dskochilov.easydinner.shared.RecipeSearchRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.json.Json

private const val MaxIngredients = 20
private const val MaxIngredientLength = 80

fun Application.module() {
    install(CallLogging)
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }

    val apiKey = System.getenv("SPOONACULAR_API_KEY").orEmpty()
    val spoonacularClient = if (apiKey.isBlank()) {
        null
    } else {
        HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 15_000
            }
            install(ClientContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
        }
    }
    val recipeProvider = if (apiKey.isBlank()) {
        MockRecipeProvider()
    } else {
        SpoonacularRecipeProvider(apiKey, checkNotNull(spoonacularClient))
    }

    monitor.subscribe(ApplicationStopped) {
        spoonacularClient?.close()
    }

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        post("/recipes/search") {
            val request = runCatching {
                call.receive<RecipeSearchRequestDto>().normalized()
            }.getOrElse { error ->
                val message = error.message ?: "Invalid request"
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to message))
                return@post
            }

            runCatching {
                recipeProvider.search(request)
            }.onSuccess { response ->
                call.respond(response)
            }.onFailure { error ->
                call.respond(error.toGatewayStatus(), mapOf("error" to error.toClientMessage()))
            }
        }
    }
}

private fun RecipeSearchRequestDto.normalized(): RecipeSearchRequestDto {
    val normalizedIngredients = ingredients
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase() }

    require(normalizedIngredients.isNotEmpty()) {
        "At least one ingredient is required"
    }
    require(normalizedIngredients.size <= MaxIngredients) {
        "No more than $MaxIngredients ingredients are allowed"
    }
    require(normalizedIngredients.all { it.length <= MaxIngredientLength }) {
        "Ingredient names must be $MaxIngredientLength characters or fewer"
    }

    return copy(ingredients = normalizedIngredients)
}

private fun Throwable.toGatewayStatus(): HttpStatusCode = when (this) {
    is HttpRequestTimeoutException,
    is TimeoutCancellationException -> HttpStatusCode.GatewayTimeout
    else -> HttpStatusCode.BadGateway
}

private fun Throwable.toClientMessage(): String = when (this) {
    is HttpRequestTimeoutException,
    is TimeoutCancellationException -> "Recipe provider timed out"
    else -> "Recipe provider is unavailable"
}
