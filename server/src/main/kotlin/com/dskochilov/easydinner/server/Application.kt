package com.dskochilov.easydinner.server

import com.dskochilov.easydinner.server.recipe.MockRecipeProvider
import com.dskochilov.easydinner.server.recipe.SpoonacularRecipeProvider
import com.dskochilov.easydinner.shared.RecipeSearchRequestDto
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun Application.module() {
    install(CallLogging)
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }

    val apiKey = System.getenv("SPOONACULAR_API_KEY").orEmpty()
    val recipeProvider = if (apiKey.isBlank()) {
        MockRecipeProvider()
    } else {
        SpoonacularRecipeProvider(apiKey)
    }

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        post("/recipes/search") {
            val request = call.receive<RecipeSearchRequestDto>()
            if (request.ingredients.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "At least one ingredient is required"))
                return@post
            }

            call.respond(recipeProvider.search(request))
        }
    }
}
