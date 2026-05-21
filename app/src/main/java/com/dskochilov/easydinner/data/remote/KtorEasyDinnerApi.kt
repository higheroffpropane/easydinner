package com.dskochilov.easydinner.data.remote

import com.dskochilov.easydinner.shared.RecipeSearchRequestDto
import com.dskochilov.easydinner.shared.RecipeSearchResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorEasyDinnerApi(
    private val client: HttpClient,
    baseUrl: String,
) : EasyDinnerApi {

    private val recipesSearchUrl = "${baseUrl.trimEnd('/')}/recipes/search"

    override suspend fun searchRecipes(request: RecipeSearchRequestDto): RecipeSearchResponseDto =
        client.post(recipesSearchUrl) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
