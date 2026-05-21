package com.dskochilov.easydinner.presentation.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.dskochilov.easydinner.presentation.navigation.RefreshablePage

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    var aiApiKey by rememberSaveable { mutableStateOf("") }
    var spoonacularApiKey by rememberSaveable { mutableStateOf("") }
    var notificationsEnabled by rememberSaveable { mutableStateOf(true) }

    RefreshablePage(
        title = "Settings",
        modifier = modifier,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SubscriptionCard()
            }
            item {
                SettingsRows(
                    notificationsEnabled = notificationsEnabled,
                    onNotificationsChange = { notificationsEnabled = it },
                )
            }
            item {
                ApiKeyFields(
                    aiApiKey = aiApiKey,
                    onAiApiKeyChange = { aiApiKey = it },
                    spoonacularApiKey = spoonacularApiKey,
                    onSpoonacularApiKeyChange = { spoonacularApiKey = it },
                )
            }
        }
    }
}

@Composable
private fun SubscriptionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Active subscription",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "18 days left",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = "$4.99 / month",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Buy subscription")
            }
        }
    }
}

@Composable
private fun SettingsRows(
    notificationsEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
) {
    Column {
        ListItem(
            headlineContent = { Text("Cooking preferences") },
            supportingContent = { Text("Servings, time and kitchen equipment defaults") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text("Recipe notifications") },
            supportingContent = { Text("Reminders and saved recipe updates") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            trailingContent = {
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationsChange,
                )
            },
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text("API credentials") },
            supportingContent = { Text("Keys are stored here before persistence is added") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
        )
    }
}

@Composable
private fun ApiKeyFields(
    aiApiKey: String,
    onAiApiKeyChange: (String) -> Unit,
    spoonacularApiKey: String,
    onSpoonacularApiKeyChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = aiApiKey,
            onValueChange = onAiApiKeyChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("AI API key") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(8.dp),
        )
        OutlinedTextField(
            value = spoonacularApiKey,
            onValueChange = onSpoonacularApiKeyChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Spoonacular API key") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(8.dp),
        )
    }
}
