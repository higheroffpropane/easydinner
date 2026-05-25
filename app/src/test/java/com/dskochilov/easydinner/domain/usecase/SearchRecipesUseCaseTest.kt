package com.dskochilov.easydinner.domain.usecase

import com.dskochilov.easydinner.domain.model.CookingPreferences
import com.dskochilov.easydinner.domain.model.Ingredient
import com.dskochilov.easydinner.domain.model.IngredientSource
import com.dskochilov.easydinner.domain.model.RecipeSearchResult
import com.dskochilov.easydinner.domain.model.RecipeSource
import com.dskochilov.easydinner.domain.repository.RecipeRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SearchRecipesUseCaseTest {

    private val preferences = CookingPreferences(
        maxReadyTimeMinutes = 30,
        servings = 2,
        useOven = false,
    )

    @Test
    fun `normalizes ingredient text before searching`() = runTest {
        val repository = FakeRecipeRepository()
        val useCase = SearchRecipesUseCase(repository)

        val result = useCase(" eggs, Cheese;\nEGGS ; tomato ", preferences)

        assertTrue(result.isSuccess)
        assertEquals(
            listOf(
                Ingredient("eggs", IngredientSource.UserText),
                Ingredient("Cheese", IngredientSource.UserText),
                Ingredient("tomato", IngredientSource.UserText),
            ),
            repository.receivedIngredients,
        )
        assertEquals(preferences, repository.receivedPreferences)
    }

    @Test
    fun `returns failure when ingredient text is empty`() = runTest {
        val repository = FakeRecipeRepository()
        val useCase = SearchRecipesUseCase(repository)

        val result = useCase(" , ; \n ", preferences)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals(0, repository.callCount)
    }

    @Test
    fun `returns failure when repository throws regular error`() = runTest {
        val error = IllegalStateException("backend is unavailable")
        val repository = FakeRecipeRepository(error = error)
        val useCase = SearchRecipesUseCase(repository)

        val result = useCase("eggs", preferences)

        assertTrue(result.isFailure)
        assertSame(error, result.exceptionOrNull())
    }

    @Test
    fun `rethrows cancellation from repository`() = runTest {
        val error = CancellationException("cancelled")
        val repository = FakeRecipeRepository(error = error)
        val useCase = SearchRecipesUseCase(repository)

        try {
            useCase("eggs", preferences)
            fail("Expected CancellationException")
        } catch (actual: CancellationException) {
            assertSame(error, actual)
        }
    }

    private class FakeRecipeRepository(
        private val error: Throwable? = null,
    ) : RecipeRepository {
        var callCount = 0
            private set
        var receivedIngredients: List<Ingredient> = emptyList()
            private set
        var receivedPreferences: CookingPreferences? = null
            private set

        override suspend fun searchRecipes(
            ingredients: List<Ingredient>,
            preferences: CookingPreferences,
        ): RecipeSearchResult {
            callCount += 1
            receivedIngredients = ingredients
            receivedPreferences = preferences
            error?.let { throw it }

            return RecipeSearchResult(
                recipes = emptyList(),
                source = RecipeSource.Mock,
            )
        }
    }
}
