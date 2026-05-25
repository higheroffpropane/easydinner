package com.dskochilov.easydinner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import android.view.WindowInsets
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dskochilov.easydinner.presentation.TestTags
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
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

    @Test
    fun footerTabsStayAboveNavigationBar() = run {
        val navigationBarBottomInset = composeTestRule.activity.window.decorView.rootWindowInsets
            ?.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars())
            ?.bottom
            ?: 0
        assumeTrue("Device does not report a bottom navigation bar inset", navigationBarBottomInset > 0)

        step("Footer tab is above the navigation bar inset") {
            val rootBottom = composeTestRule.onRoot().getUnclippedBoundsInRoot().bottom
            val tabBottom = composeTestRule
                .onNodeWithTag(TestTags.TAB_AI)
                .getUnclippedBoundsInRoot()
                .bottom
            val navigationBarBottomInsetDp = with(composeTestRule.density) {
                navigationBarBottomInset.toDp()
            }

            assertTrue(
                "Footer tab bottom $tabBottom must be above navigation bar inset $navigationBarBottomInsetDp",
                tabBottom <= rootBottom - navigationBarBottomInsetDp,
            )
        }
    }

    @Test
    fun textScreenFillsAvailableSpaceAboveFooter() = run {
        step("Open text search tab") {
            composeTestRule
                .onNodeWithTag(TestTags.TAB_TEXT)
                .performClick()
        }

        step("Screen reaches the footer without an extra navigation bar gap") {
            val screenBottom = composeTestRule
                .onNodeWithTag(TestTags.RECIPE_SEARCH_SCREEN)
                .getUnclippedBoundsInRoot()
                .bottom
            val textTabTop = composeTestRule
                .onNodeWithTag(TestTags.TAB_TEXT)
                .getUnclippedBoundsInRoot()
                .top
            val footerVerticalPadding = 6.dp

            assertTrue(
                "Screen bottom $screenBottom must reach footer top near ${textTabTop - footerVerticalPadding}",
                screenBottom >= textTabTop - footerVerticalPadding,
            )
        }
    }
}
