package com.dskochilov.easydinner.presentation.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dskochilov.easydinner.domain.model.Recipe

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    RecipeMeta(recipe)
                }
            }
            if (recipe.usedIngredients.isNotEmpty()) {
                IngredientChips(
                    title = "Есть",
                    ingredients = recipe.usedIngredients,
                )
            }
            if (recipe.missedIngredients.isNotEmpty()) {
                IngredientChips(
                    title = "Докупи",
                    ingredients = recipe.missedIngredients,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailBottomSheet(
    recipe: Recipe,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val summary = recipe.summary.toPlainText()
    val instructions = recipe.instructions.toPlainText()
    val allIngredients = recipe.allIngredients.ifEmpty {
        recipe.usedIngredients + recipe.missedIngredients
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = recipe.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        RecipeMeta(recipe)
                    }
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                        )
                    }
                }
            }

            if (summary.isNotBlank()) {
                item {
                    DetailSection(
                        title = "Summary",
                        body = summary,
                    )
                }
            }

            if (instructions.isNotBlank()) {
                item {
                    DetailSection(
                        title = "Instructions",
                        body = instructions,
                    )
                }
            }

            if (allIngredients.isNotEmpty()) {
                item {
                    IngredientChips(
                        title = "Ingredients",
                        ingredients = allIngredients,
                    )
                }
            }

            if (recipe.dishTypes.isNotEmpty()) {
                item {
                    IngredientChips(
                        title = "Dish types",
                        ingredients = recipe.dishTypes,
                    )
                }
            }

            if (recipe.diets.isNotEmpty()) {
                item {
                    IngredientChips(
                        title = "Diets",
                        ingredients = recipe.diets,
                    )
                }
            }

            if (recipe.sourceUrl?.isNotBlank() == true) {
                item {
                    DetailSection(
                        title = "Source",
                        body = recipe.sourceUrl,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    body: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RecipeMeta(recipe: Recipe) {
    val parts = buildList {
        recipe.readyInMinutes?.let { add("$it мин") }
        recipe.servings?.let { add("$it порц.") }
    }
    Text(
        text = parts.joinToString(" • "),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IngredientChips(
    title: String,
    ingredients: List<String>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ingredients.take(8).forEach { ingredient ->
                ElevatedAssistChip(
                    onClick = {},
                    label = { Text(ingredient) },
                )
            }
        }
    }
}

private fun String?.toPlainText(): String = this
    ?.replace(Regex("<[^>]+>"), " ")
    ?.replace("&nbsp;", " ")
    ?.replace("&amp;", "&")
    ?.replace("&quot;", "\"")
    ?.replace("&#39;", "'")
    ?.replace(Regex("\\s+"), " ")
    ?.trim()
    .orEmpty()
