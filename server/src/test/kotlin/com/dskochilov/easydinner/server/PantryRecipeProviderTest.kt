package com.dskochilov.easydinner.server

import com.dskochilov.easydinner.server.recipe.PantryRecipeProvider
import com.dskochilov.easydinner.shared.CookingPreferencesDto
import com.dskochilov.easydinner.shared.RecipeSearchRequestDto
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PantryRecipeProviderTest {
    private val provider = PantryRecipeProvider()

    @Test
    fun matchesRussianIngredientsAndScalesDirections() = runBlocking {
        val result = provider.search(RecipeSearchRequestDto(
            ingredients = listOf(" Яйца ", "шпинат", "сыр"),
            preferences = CookingPreferencesDto(maxReadyTimeMinutes = 15, servings = 3),
        ))
        val best = result.recipes.first()
        assertEquals("Spinach and cheese omelette", best.title)
        assertEquals(listOf("eggs", "spinach", "cheese"), best.usedIngredients)
        assertTrue(best.missedIngredients.isEmpty())
        assertTrue("6 eggs" in best.instructions.orEmpty())
        assertTrue(result.recipes.all { it.readyInMinutes!! <= 15 })
    }

    @Test
    fun ovenAndTimeLimitsDoNotInventFasterRecipes() = runBlocking {
        val request = RecipeSearchRequestDto(listOf("potatoes"))
        val bake = provider.search(request).recipes.single()
        assertFalse(bake.id >= 0)
        for (prefs in listOf(CookingPreferencesDto(useOven = false), CookingPreferencesDto(maxReadyTimeMinutes = 20))) {
            assertTrue(provider.search(request.copy(preferences = prefs)).recipes.isEmpty())
        }
        assertTrue(provider.search(RecipeSearchRequestDto(listOf("mango"))).recipes.isEmpty())
    }
}
