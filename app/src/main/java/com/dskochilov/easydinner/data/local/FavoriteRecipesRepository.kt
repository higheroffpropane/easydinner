package com.dskochilov.easydinner.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dskochilov.easydinner.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

private val Context.favoriteRecipesDataStore by preferencesDataStore(name = "favorite_recipes")

class FavoriteRecipesRepository(
    private val context: Context,
) {
    private val favoritesKey = stringPreferencesKey("recipes")
    private val recipeListSerializer = ListSerializer(Recipe.serializer())
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    val favoriteRecipes: Flow<List<Recipe>> = context.favoriteRecipesDataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences ->
            decodeRecipes(preferences[favoritesKey].orEmpty())
        }

    suspend fun toggleFavorite(recipe: Recipe) {
        context.favoriteRecipesDataStore.edit { preferences ->
            val current = decodeRecipes(preferences[favoritesKey].orEmpty())
            val updated = if (current.any { it.id == recipe.id }) {
                current.filterNot { it.id == recipe.id }
            } else {
                listOf(recipe) + current
            }
            preferences[favoritesKey] = json.encodeToString(recipeListSerializer, updated)
        }
    }

    private fun decodeRecipes(value: String): List<Recipe> {
        if (value.isBlank()) {
            return emptyList()
        }

        return runCatching {
            json.decodeFromString(recipeListSerializer, value)
        }.getOrDefault(emptyList())
    }
}
