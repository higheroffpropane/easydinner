package com.dskochilov.easydinner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dskochilov.easydinner.presentation.TestTags
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EasyDinnerKaspressoTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport(),
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun userCanOpenTextSearchTabFromAiTab() = run {
        step("AI tab is visible on start") {
            composeTestRule
                .onNodeWithTag(TestTags.AI_TAKE_PHOTO)
                .assertIsDisplayed()
        }

        step("Open text search tab") {
            composeTestRule
                .onNodeWithTag(TestTags.TAB_TEXT)
                .assertIsDisplayed()
                .performClick()

            composeTestRule
                .onNodeWithTag(TestTags.RECIPE_SEARCH_SCREEN)
                .assertIsDisplayed()
        }
    }
}
