package com.dskochilov.easydinner.presentation.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dskochilov.easydinner.R
import com.dskochilov.easydinner.domain.model.Recipe
import com.dskochilov.easydinner.presentation.favorites.FavoriteRecipesViewModel
import com.dskochilov.easydinner.presentation.navigation.RefreshablePage
import com.dskochilov.easydinner.presentation.recipe.RecipeCard
import com.dskochilov.easydinner.presentation.recipe.RecipeDetailBottomSheet
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavouritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoriteRecipesViewModel = koinViewModel(),
) {
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsStateWithLifecycle()
    val favoriteRecipeIds by viewModel.favoriteRecipeIds.collectAsStateWithLifecycle()
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    RefreshablePage(
        title = stringResource(R.string.screen_favourites_title),
        modifier = modifier,
    ) { paddingValues ->
        if (favoriteRecipes.isEmpty()) {
            EmptyFavourites(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(
                    items = favoriteRecipes,
                    key = { recipe -> recipe.id },
                ) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { selectedRecipe = recipe },
                    )
                }
            }
        }
    }

    selectedRecipe?.let { recipe ->
        RecipeDetailBottomSheet(
            recipe = recipe,
            isFavorite = recipe.id in favoriteRecipeIds,
            onFavoriteClick = { viewModel.toggleFavorite(recipe) },
            onDismiss = { selectedRecipe = null },
        )
    }
}

@Composable
private fun EmptyFavourites(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.StarBorder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.empty_favourites),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
    }
}
