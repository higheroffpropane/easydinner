package com.dskochilov.easydinner.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dskochilov.easydinner.R
import com.dskochilov.easydinner.domain.model.Recipe
import com.dskochilov.easydinner.domain.model.RecipeSource
import com.dskochilov.easydinner.presentation.TestTags
import com.dskochilov.easydinner.presentation.favorites.FavoriteRecipesViewModel
import com.dskochilov.easydinner.presentation.navigation.RefreshablePage
import com.dskochilov.easydinner.presentation.recipe.RecipeCard
import com.dskochilov.easydinner.presentation.recipe.RecipeDetailBottomSheet
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun RecipeSearchScreen(
    modifier: Modifier = Modifier,
    aiIngredientText: String? = null,
    aiSearchRequestId: Int = 0,
    onAiSearchRequestConsumed: () -> Unit = {},
    viewModel: RecipeSearchViewModel = koinViewModel(),
    favoriteRecipesViewModel: FavoriteRecipesViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteRecipeIds by favoriteRecipesViewModel.favoriteRecipeIds.collectAsStateWithLifecycle()
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    LaunchedEffect(aiSearchRequestId, aiIngredientText) {
        val ingredients = aiIngredientText?.trim()
        if (aiSearchRequestId > 0 && !ingredients.isNullOrBlank()) {
            viewModel.searchFromAiIngredients(ingredients)
            onAiSearchRequestConsumed()
        }
    }

    RefreshablePage(
        title = stringResource(R.string.app_name),
        modifier = modifier,
    ) { paddingValues ->
        RecipeSearchContent(
            state = state,
            onIngredientTextChange = viewModel::onIngredientTextChange,
            onMaxReadyTimeChange = viewModel::onMaxReadyTimeChange,
            onServingsChange = viewModel::onServingsChange,
            onUseOvenChange = viewModel::onUseOvenChange,
            onSearchClick = viewModel::search,
            onRecipeClick = { recipe -> selectedRecipe = recipe },
            modifier = Modifier.padding(paddingValues),
        )
    }

    selectedRecipe?.let { recipe ->
        RecipeDetailBottomSheet(
            recipe = recipe,
            isFavorite = recipe.id in favoriteRecipeIds,
            onFavoriteClick = { favoriteRecipesViewModel.toggleFavorite(recipe) },
            onDismiss = { selectedRecipe = null },
        )
    }
}

@Composable
private fun RecipeSearchContent(
    state: RecipeSearchUiState,
    onIngredientTextChange: (String) -> Unit,
    onMaxReadyTimeChange: (Int) -> Unit,
    onServingsChange: (Int) -> Unit,
    onUseOvenChange: (Boolean) -> Unit,
    onSearchClick: () -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag(TestTags.RECIPE_SEARCH_SCREEN),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            IngredientInput(
                value = state.ingredientText,
                onValueChange = onIngredientTextChange,
            )
        }
        item {
            PreferencesPanel(
                maxReadyTimeMinutes = state.maxReadyTimeMinutes,
                servings = state.servings,
                useOven = state.useOven,
                onMaxReadyTimeChange = onMaxReadyTimeChange,
                onServingsChange = onServingsChange,
                onUseOvenChange = onUseOvenChange,
            )
        }
        item {
            Button(
                onClick = onSearchClick,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    if (state.isLoading) {
                        stringResource(R.string.search_loading)
                    } else {
                        stringResource(R.string.search_button)
                    },
                )
            }
        }
        state.errorMessageResId?.let { messageResId ->
            item {
                Text(
                    text = stringResource(messageResId),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        if (state.recipes.isNotEmpty()) {
            item {
                SearchSourceLabel(state.source)
            }
            items(
                items = state.recipes,
                key = { recipe -> recipe.id },
            ) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = { onRecipeClick(recipe) },
                )
            }
        }
    }
}

@Composable
private fun IngredientInput(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.ingredients_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            placeholder = {
                Text(stringResource(R.string.ingredients_placeholder))
            },
            shape = RoundedCornerShape(8.dp),
        )
    }
}

@Composable
private fun PreferencesPanel(
    maxReadyTimeMinutes: Int,
    servings: Int,
    useOven: Boolean,
    onMaxReadyTimeChange: (Int) -> Unit,
    onServingsChange: (Int) -> Unit,
    onUseOvenChange: (Boolean) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.preferences_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(R.string.time_label))
                    Text(stringResource(R.string.minutes_value, maxReadyTimeMinutes))
                }
                Slider(
                    value = maxReadyTimeMinutes.toFloat(),
                    onValueChange = { onMaxReadyTimeChange(it.roundToInt()) },
                    valueRange = 10f..120f,
                    steps = 10,
                )
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.servings_label))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onServingsChange(servings - 1) },
                        enabled = servings > 1,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp),
                    ) {
                        Text("-")
                    }
                    Text(
                        text = servings.toString(),
                        modifier = Modifier.align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Button(
                        onClick = { onServingsChange(servings + 1) },
                        enabled = servings < 8,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp),
                    ) {
                        Text("+")
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.oven_label))
                Switch(
                    checked = useOven,
                    onCheckedChange = onUseOvenChange,
                )
            }
        }
    }
}

@Composable
private fun SearchSourceLabel(source: RecipeSource?) {
    val label = when (source) {
        RecipeSource.Mock -> stringResource(R.string.source_mock)
        RecipeSource.Spoonacular -> "Spoonacular"
        null -> ""
    }

    if (label.isNotBlank()) {
        Text(
            text = stringResource(R.string.source_label, label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
