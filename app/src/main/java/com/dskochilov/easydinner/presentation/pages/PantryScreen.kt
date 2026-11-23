package com.dskochilov.easydinner.presentation.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dskochilov.easydinner.R
import com.dskochilov.easydinner.presentation.navigation.DinnerPage

@Composable
fun PantryScreen(onSearch: (String) -> Unit, modifier: Modifier = Modifier) {
    DinnerPage(title = stringResource(R.string.pantry_title), modifier = modifier) { padding ->
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.pantry_tip), style = MaterialTheme.typography.bodyLarge)
            listOf(R.string.pantry_eggs, R.string.pantry_pasta, R.string.pantry_rice).forEach { preset ->
                val ingredients = stringResource(preset)
                OutlinedButton(onClick = { onSearch(ingredients) }, modifier = Modifier.fillMaxWidth()) {
                    Text(ingredients)
                }
            }
        }
    }
}
