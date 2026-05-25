package com.dskochilov.easydinner.server

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun healthReturnsOk() = testApplication {
        application {
            module()
        }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun searchRejectsBlankIngredients() = testApplication {
        application {
            module()
        }

        val response = client.post("/recipes/search") {
            contentType(ContentType.Application.Json)
            setBody("""{"ingredients":["","   "]}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun searchReturnsMockRecipesWithoutApiKey() = testApplication {
        application {
            module()
        }

        val response = client.post("/recipes/search") {
            contentType(ContentType.Application.Json)
            setBody("""{"ingredients":["eggs","cheese"],"preferences":{"servings":2}}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
