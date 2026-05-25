package com.dskochilov.easydinner.presentation.search

import com.dskochilov.easydinner.MainDispatcherRule
import com.dskochilov.easydinner.R
import com.dskochilov.easydinner.domain.model.CookingPreferences
import com.dskochilov.easydinner.domain.model.Ingredient
import com.dskochilov.easydinner.domain.model.Recipe
import com.dskochilov.easydinner.domain.model.RecipeSearchResult
import com.dskochilov.easydinner.domain.model.RecipeSource
import com.dskochilov.easydinner.domain.repository.RecipeRepository
import com.dskochilov.easydinner.domain.usecase.SearchRecipesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeSearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `servings are clamped to supported range`() {
        val viewModel = RecipeSearchViewModel(SearchRecipesUseCase(FakeRecipeRepository()))

        viewModel.onServingsChange(0)
        assertEquals(1, viewModel.uiState.value.servings)

        viewModel.onServingsChange(12)
        assertEquals(8, viewModel.uiState.value.servings)
    }

    @Test
    fun `ingredient text change clears previous error`() {
        val viewModel = RecipeSearchViewModel(SearchRecipesUseCase(FakeRecipeRepository()))

        viewModel.search()

        assertEquals(R.string.search_error_empty_ingredients, viewModel.uiState.value.errorMessageResId)

        viewModel.onIngredientTextChange("eggs")

        assertNull(viewModel.uiState.value.errorMessageResId)
        assertEquals("eggs", viewModel.uiState.value.ingredientText)
    }

    @Test
    fun `search publishes recipes and source`() = runTest {
        val recipes = listOf(testRecipe(id = 1, title = "Tomato omelette"))
        val repository = FakeRecipeRepository(
            result = RecipeSearchResult(
                recipes = recipes,
                source = RecipeSource.Mock,
            ),
        )
        val viewModel = RecipeSearchViewModel(SearchRecipesUseCase(repository))

        viewModel.onIngredientTextChange("eggs, tomato")
        viewModel.onMaxReadyTimeChange(20)
        viewModel.onServingsChange(3)
        viewModel.onUseOvenChange(true)
        viewModel.search()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessageResId)
        assertEquals(recipes, state.recipes)
        assertEquals(RecipeSource.Mock, state.source)
        assertEquals(
            CookingPreferences(
                maxReadyTimeMinutes = 20,
                servings = 3,
                useOven = true,
            ),
            repository.receivedPreferences,
        )
    }

    @Test
    fun `blank search shows empty ingredients error`() = runTest {
        val viewModel = RecipeSearchViewModel(SearchRecipesUseCase(FakeRecipeRepository()))

        viewModel.search()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(R.string.search_error_empty_ingredients, state.errorMessageResId)
    }

    @Test
    fun `ai search resets preferences and starts search`() = runTest {
        val repository = FakeRecipeRepository()
        val viewModel = RecipeSearchViewModel(SearchRecipesUseCase(repository))

        viewModel.onMaxReadyTimeChange(90)
        viewModel.onServingsChange(8)
        viewModel.onUseOvenChange(true)
        viewModel.searchFromAiIngredients("eggs, cheese")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("eggs, cheese", state.ingredientText)
        assertEquals(RecipeSearchUiState.DefaultMaxReadyTimeMinutes, state.maxReadyTimeMinutes)
        assertEquals(RecipeSearchUiState.DefaultServings, state.servings)
        assertEquals(RecipeSearchUiState.DefaultUseOven, state.useOven)
        assertEquals(
            CookingPreferences(
                maxReadyTimeMinutes = RecipeSearchUiState.DefaultMaxReadyTimeMinutes,
                servings = RecipeSearchUiState.DefaultServings,
                useOven = RecipeSearchUiState.DefaultUseOven,
            ),
            repository.receivedPreferences,
        )
    }

    private class FakeRecipeRepository(
        private val result: RecipeSearchResult = RecipeSearchResult(
            recipes = emptyList(),
            source = RecipeSource.Mock,
        ),
    ) : RecipeRepository {
        var receivedPreferences: CookingPreferences? = null
            private set

        override suspend fun searchRecipes(
            ingredients: List<Ingredient>,
            preferences: CookingPreferences,
        ): RecipeSearchResult {
            receivedPreferences = preferences
            return result
        }
    }
}

private fun testRecipe(
    id: Int,
    title: String,
): Recipe = Recipe(
    id = id,
    title = title,
    imageUrl = null,
    readyInMinutes = 20,
    servings = 2,
    usedIngredients = listOf("eggs"),
    missedIngredients = listOf("tomato"),
    allIngredients = listOf("eggs", "tomato"),
    summary = null,
    instructions = null,
    dishTypes = emptyList(),
    diets = emptyList(),
    sourceUrl = null,
)
