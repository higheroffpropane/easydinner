package com.dskochilov.easydinner.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dskochilov.easydinner.domain.model.CookingPreferences
import com.dskochilov.easydinner.domain.usecase.SearchRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeSearchViewModel(
    private val searchRecipes: SearchRecipesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeSearchUiState())
    val uiState: StateFlow<RecipeSearchUiState> = _uiState

    fun onIngredientTextChange(value: String) {
        _uiState.update {
            it.copy(
                ingredientText = value,
                errorMessage = null,
            )
        }
    }

    fun onMaxReadyTimeChange(value: Int) {
        _uiState.update { it.copy(maxReadyTimeMinutes = value) }
    }

    fun onServingsChange(value: Int) {
        _uiState.update { it.copy(servings = value.coerceIn(1, 8)) }
    }

    fun onUseOvenChange(value: Boolean) {
        _uiState.update { it.copy(useOven = value) }
    }

    fun search() {
        val state = _uiState.value
        search(
            ingredientText = state.ingredientText,
            preferences = CookingPreferences(
                maxReadyTimeMinutes = state.maxReadyTimeMinutes,
                servings = state.servings,
                useOven = state.useOven,
            ),
        )
    }

    fun searchFromAiIngredients(value: String) {
        val preferences = CookingPreferences(
            maxReadyTimeMinutes = RecipeSearchUiState.DefaultMaxReadyTimeMinutes,
            servings = RecipeSearchUiState.DefaultServings,
            useOven = RecipeSearchUiState.DefaultUseOven,
        )

        _uiState.update {
            it.copy(
                ingredientText = value,
                maxReadyTimeMinutes = preferences.maxReadyTimeMinutes,
                servings = preferences.servings,
                useOven = preferences.useOven,
                errorMessage = null,
            )
        }

        search(
            ingredientText = value,
            preferences = preferences,
        )
    }

    private fun search(
        ingredientText: String,
        preferences: CookingPreferences,
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            searchRecipes(ingredientText, preferences)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recipes = result.recipes,
                            source = result.source,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Не удалось найти рецепты",
                        )
                    }
                }
        }
    }
}
