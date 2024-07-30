package com.kristianskokars.catnexus.feature

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.kristianskokars.catnexus.MainActivity
import com.kristianskokars.catnexus.core.presentation.theme.SimpleCatAppTheme
import org.junit.Rule
import org.junit.Test

class UITestExample {
    // TODO: this can be used to get the Activity if you need, otherwise you can test things in isolation
//    val composeRule = createAndroidComposeRule<MainActivity>()
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun testUI_thingLoaded() {
        composeRule.setContent {
            SimpleCatAppTheme {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Test")
                }
            }
        }

        composeRule.onNodeWithText("Test").assertIsDisplayed()
        composeRule.onNode(
            hasText("Test").and(hasClickAction())
        ).assertIsDisplayed()
    }
}