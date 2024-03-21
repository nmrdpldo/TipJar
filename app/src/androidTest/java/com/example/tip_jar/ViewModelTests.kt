package com.example.tip_jar

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewModelScope
import com.example.tip_jar.database.entity.TipHistory
import com.example.tip_jar.di.repository.SavedPaymentsRepository
import com.example.tip_jar.ui.composables.toDateString
import com.example.tip_jar.viewmodel.TipJarViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.createTestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltAndroidTest
class ViewModelTests {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    fun performSearch() : Job = CoroutineScope(Dispatchers.Main).launch {
        viewModel.searchPaymentList()
    }

    fun performCalculate() : Job = CoroutineScope(Dispatchers.Main).launch {
        viewModel.calculateTotal()
    }

    @Test
    fun test_search_payment_list() = testScope.runTest {
        viewModel.query.value = "3564"

        val itemToSearch =
            TipHistory(
                11111,
                "3564",
                5,
                "3564",
                111.1,
                111.10,
                "",
                false
            )

        viewModel.paymentHistory.value = listOf(
            TipHistory(
                222222,
                "",
                5,
                "",
                100.0,
                100.0,
                "",
                false
            ),
            TipHistory(
                33333,
                "",
                5,
                "",
                100.0,
                100.0,
                "",
                false
            ),
            itemToSearch
        )

        performSearch().join()
        assertEquals(itemToSearch, viewModel.paymentHistory.value[0])
    }



    @Test
    fun test_search_payment_timestamp() = testScope.runTest {
        viewModel.query.value = "january 25"

        val itemToSearch =
            TipHistory(
                1706155220000, // this converts to January 25 2024
                "0",
                1,
                "0",
                0.0,
                0.0,
                "",
                false
            )

        viewModel.paymentHistory.value = listOf(
            TipHistory(
                222222,
                "",
                5,
                "",
                100.0,
                100.0,
                "",
                false
            ),
            TipHistory(
                33333,
                "",
                5,
                "",
                100.0,
                100.0,
                "",
                false
            ),
            itemToSearch
        )

        performSearch().join()
        assertEquals(itemToSearch, viewModel.paymentHistory.value[0])
    }

    @Test
    fun test_search_no_item_found() = testScope.runTest{
        viewModel.query.value = "99999"

        val itemToSearch =
            TipHistory(
                111111,
                "",
                5,
                "",
                100.0,
                100.0,
                "",
                false
            )

        viewModel.paymentHistory.value = listOf(
            itemToSearch,
            TipHistory(
                222222,
                "",
                5,
                "",
                100.0,
                100.0,
                "",
                false
            ),
            TipHistory(
                33333,
                "",
                5,
                "",
                100.0,
                100.0,
                "",
                false
            )
        )

        performSearch().join()
        assertEquals(0, viewModel.paymentHistory.value.size)
    }


    @Test
    fun test_save_payment_calculation_tip_invalid() = testScope.runTest {
        viewModel.amountValue.value = "100"
        viewModel.tipValue.value = "/*-rt"
        viewModel.peopleCount.value = 1
        performCalculate().join()
        Assert.assertEquals(0.0, viewModel.totalTip.value, 0.01)
        Assert.assertEquals(0.0, viewModel.perPerson.value, 0.01)
    }


    @Test
    fun test_save_payment_calculation_amount_invalid() = testScope.runTest {
        viewModel.amountValue.value = "abcde"
        viewModel.tipValue.value = "11"
        viewModel.peopleCount.value = 1
        performCalculate().join()
        Assert.assertEquals(0.0, viewModel.totalTip.value, 0.01)
        Assert.assertEquals(0.0, viewModel.perPerson.value, 0.01)
    }
    @Test
    fun test_save_payment_calculation_tip_empty() = testScope.runTest {
        viewModel.amountValue.value = "100"
        viewModel.tipValue.value = ""
        viewModel.peopleCount.value = 1
        performCalculate().join()
        Assert.assertEquals(0.0, viewModel.totalTip.value, 0.01)
        Assert.assertEquals(0.0, viewModel.perPerson.value, 0.01)
    }


    @Test
    fun test_save_payment_calculation_amount_empty() = testScope.runTest {
        viewModel.amountValue.value = ""
        viewModel.tipValue.value = "11"
        viewModel.peopleCount.value = 1
        performCalculate().join()
        Assert.assertEquals(0.0, viewModel.totalTip.value, 0.01)
        Assert.assertEquals(0.0, viewModel.perPerson.value, 0.01)
    }


    @Test
    fun test_save_payment_calculation_success() = testScope.runTest {
        viewModel.amountValue.value = "1234.56"
        viewModel.tipValue.value = "13.6"
        viewModel.peopleCount.value = 3
        performCalculate().join()
        Assert.assertEquals(167.90016, viewModel.totalTip.value, 0.01)
        Assert.assertEquals(55.96672, viewModel.perPerson.value, 0.01)
    }


    @Test
    fun test_save_payment_calculation_negative_success() = testScope.runTest {
        viewModel.amountValue.value = "-789.3"
        viewModel.tipValue.value = "-8.3"
        viewModel.peopleCount.value = 5
        performCalculate().join()
        Assert.assertEquals(65.5119, viewModel.totalTip.value, 0.01)
        Assert.assertEquals(13.10238, viewModel.perPerson.value, 0.01)
    }
}