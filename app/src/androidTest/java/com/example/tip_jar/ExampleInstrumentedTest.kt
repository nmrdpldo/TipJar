package com.example.tip_jar

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.example.tip_jar.di.repository.SavedPaymentsRepository
import com.example.tip_jar.ui.composables.TipJarMainScreen
import com.example.tip_jar.utils.TestTags
import com.example.tip_jar.viewmodel.TipJarViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

import org.junit.Test

import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@HiltAndroidTest
class ExampleInstrumentedTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var repository: SavedPaymentsRepository

    private lateinit var viewModel: TipJarViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = TipJarViewModel(repository)
    }


    @Test
    fun ui_test_save_payment_empty_amount(){
        composeTestRule.setContent {
            TipJarMainScreen(navController = rememberNavController(), viewModel)
        }

        composeTestRule.onNode(hasTestTag(TestTags.AmountValueTestTags)).performTextClearance()
        composeTestRule.onNode(hasTestTag(TestTags.TipValueTestTags)).performTextClearance()
        composeTestRule.onNode(hasTestTag(TestTags.TipValueTestTags)).performTextInput("10")
        composeTestRule.onNode(hasTestTag(TestTags.SavePaymentButton)).performClick()
        composeTestRule.onNode(hasText("Amount is invalid.", ignoreCase = true)).assertIsDisplayed()
    }

    @Test
    fun ui_test_save_payment_empty_tip(){
        composeTestRule.setContent {
            TipJarMainScreen(navController = rememberNavController(), viewModel)
        }

        composeTestRule.onNode(hasTestTag(TestTags.AmountValueTestTags)).performTextInput("100")
        composeTestRule.onNode(hasTestTag(TestTags.TipValueTestTags)).performTextClearance()
        composeTestRule.onNode(hasTestTag(TestTags.SavePaymentButton)).performClick()
        composeTestRule.onNode(hasText("Tip percent is invalid.", ignoreCase = true)).assertIsDisplayed()
    }

    @Test
    fun ui_test_save_payment_invalid_amount(){
        composeTestRule.setContent {
            TipJarMainScreen(navController = rememberNavController(), viewModel)
        }

        composeTestRule.onNode(hasTestTag(TestTags.AmountValueTestTags)).performTextInput("asda!@23")
        composeTestRule.onNode(hasTestTag(TestTags.TipValueTestTags)).performTextClearance()
        composeTestRule.onNode(hasTestTag(TestTags.TipValueTestTags)).performTextInput("13.3")
        composeTestRule.onNode(hasTestTag(TestTags.SavePaymentButton)).performClick()
        composeTestRule.onNode(hasText("Amount is invalid.", ignoreCase = true)).assertIsDisplayed()
    }

    @Test
    fun ui_test_save_payment_invalid_tip(){
        composeTestRule.setContent {
            TipJarMainScreen(navController = rememberNavController(), viewModel)
        }

        composeTestRule.onNode(hasTestTag(TestTags.AmountValueTestTags)).performTextInput("100")
        composeTestRule.onNode(hasTestTag(TestTags.TipValueTestTags)).performTextClearance()
        composeTestRule.onNode(hasTestTag(TestTags.TipValueTestTags)).performTextInput("13.3zxc")
        composeTestRule.onNode(hasTestTag(TestTags.SavePaymentButton)).performClick()
        composeTestRule.onNode(hasText("Tip percent is invalid.", ignoreCase = true)).assertIsDisplayed()
    }


}