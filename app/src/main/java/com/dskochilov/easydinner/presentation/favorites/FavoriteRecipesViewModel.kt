package com.dskochilov.easydinner.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dskochilov.easydinner.data.local.FavoriteRecipesRepository
import com.dskochilov.easydinner.domain.model.Recipe
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteRecipesViewModel(
    private val repository: FavoriteRecipesRepository,
) : ViewModel() {
    val favoriteRecipes: StateFlow<List<Recipe>> = repository.favoriteRecipes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val favoriteRecipeIds: StateFlow<Set<Int>> = favoriteRecipes
        .map { recipes -> recipes.map { it.id }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            repository.toggleFavorite(recipe)
        }
    }
}
