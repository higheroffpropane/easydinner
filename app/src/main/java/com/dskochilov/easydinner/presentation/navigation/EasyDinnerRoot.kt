package com.dskochilov.easydinner.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dskochilov.easydinner.presentation.pages.AiFridgeScreen
import com.dskochilov.easydinner.presentation.pages.FavouritesScreen
import com.dskochilov.easydinner.presentation.pages.SettingsScreen
import com.dskochilov.easydinner.presentation.search.RecipeSearchScreen
import kotlinx.coroutines.launch

@Composable
fun EasyDinnerRoot() {
    val tabs = remember { EasyDinnerTab.entries.toList() }
    val pagerState = rememberPagerState(
        initialPage = EasyDinnerTab.Ai.ordinal,
        pageCount = { tabs.size },
    )
    val coroutineScope = rememberCoroutineScope()
    var selectedPage by remember { mutableIntStateOf(pagerState.currentPage) }
    var aiIngredientText by remember { mutableStateOf<String?>(null) }
    var aiSearchRequestId by remember { mutableIntStateOf(0) }

    LaunchedEffect(pagerState.currentPage) {
        selectedPage = pagerState.currentPage
    }

    Scaffold(
        bottomBar = {
            EasyDinnerFooter(
                tabs = tabs,
                selectedIndex = selectedPage,
                onTabClick = { index ->
                    selectedPage = index
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        },
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            beyondViewportPageCount = 1,
            verticalAlignment = Alignment.Top,
        ) { page ->
            when (tabs[page]) {
                EasyDinnerTab.Ai -> AiFridgeScreen(
                    onSearchIngredients = { ingredients ->
                        aiIngredientText = ingredients
                        aiSearchRequestId += 1
                        selectedPage = EasyDinnerTab.Text.ordinal
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(EasyDinnerTab.Text.ordinal)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
                EasyDinnerTab.Text -> RecipeSearchScreen(
                    modifier = Modifier.fillMaxSize(),
                    aiIngredientText = aiIngredientText,
                    aiSearchRequestId = aiSearchRequestId,
                    onAiSearchRequestConsumed = {
                        aiIngredientText = null
                    },
                )
                EasyDinnerTab.Favourites -> FavouritesScreen(modifier = Modifier.fillMaxSize())
                EasyDinnerTab.Settings -> SettingsScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun EasyDinnerFooter(
    tabs: List<EasyDinnerTab>,
    selectedIndex: Int,
    onTabClick: (Int) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            ) {
                tabs.forEachIndexed { index, tab ->
                    FooterItem(
                        tab = tab,
                        selected = selectedIndex == index,
                        onClick = { onTabClick(index) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun FooterItem(
    tab: EasyDinnerTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val itemBackground = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Box(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                color = itemBackground,
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.label,
                    tint = contentColor,
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                        .size(22.dp),
                )
            }
            Text(
                text = tab.label,
                color = contentColor,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

private enum class EasyDinnerTab(
    val label: String,
    val icon: ImageVector,
) {
    Ai(
        label = "AI",
        icon = Icons.Filled.AutoAwesome,
    ),
    Text(
        label = "Text",
        icon = Icons.Filled.TextFields,
    ),
    Favourites(
        label = "Favourites",
        icon = Icons.Filled.Star,
    ),
    Settings(
        label = "Settings",
        icon = Icons.Filled.Settings,
    ),
}
