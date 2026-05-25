package com.dskochilov.easydinner.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dskochilov.easydinner.domain.model.Recipe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteRecipesRepositoryTest {

    @Test
    fun toggleFavoriteAddsAndRemovesRecipe() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val repository = FavoriteRecipesRepository(context)
        val recipe = testRecipe()

        if (repository.favoriteRecipes.first().any { it.id == recipe.id }) {
            repository.toggleFavorite(recipe)
        }

        repository.toggleFavorite(recipe)
        assertTrue(repository.favoriteRecipes.first().any { it.id == recipe.id })

        repository.toggleFavorite(recipe)
        assertFalse(repository.favoriteRecipes.first().any { it.id == recipe.id })
    }

    private fun testRecipe(): Recipe = Recipe(
        id = 976_431,
        title = "Instrumented omelette",
        imageUrl = null,
        readyInMinutes = 15,
        servings = 1,
        usedIngredients = listOf("eggs"),
        missedIngredients = emptyList(),
        allIngredients = listOf("eggs"),
        summary = null,
        instructions = "Cook eggs.",
        dishTypes = listOf("breakfast"),
        diets = emptyList(),
        sourceUrl = null,
    )
}
