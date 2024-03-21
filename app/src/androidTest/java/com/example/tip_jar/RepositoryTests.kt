package com.example.tip_jar

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.tip_jar.database.TipDatabase
import com.example.tip_jar.database.dao.TipHistoryDao
import com.example.tip_jar.database.entity.TipHistory
import com.example.tip_jar.di.repository.SavedPaymentsRepository
import com.example.tip_jar.viewmodel.TipJarViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class RepositoryTests {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: TipDatabase

    @Inject
    lateinit var repository: SavedPaymentsRepository

    @Inject
    lateinit var tipHistoryDao: TipHistoryDao

    @Before
    fun setup() {
        hiltRule.inject()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TipDatabase::class.java
        ).allowMainThreadQueries().build()
        tipHistoryDao = database.tipHistoryDao()
        repository = SavedPaymentsRepository(tipHistoryDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun test__get_empty_payment() = runBlocking {
        val paymentHistory = repository.paymentHistory()

        assertEquals(0, paymentHistory.size)
    }

    @Test
    fun test_add_and_get_payment() = runBlocking {
        val tipHistory = TipHistory(
            123456789,
            "",
            5,
            "",
            100.0,
            100.0,
            "",
            false
        )

        repository.addPayment(tipHistory)

        val paymentHistory = repository.paymentHistory()

        assertEquals(1, paymentHistory.size)
        assertEquals(tipHistory, paymentHistory[0])
    }

    @Test
    fun test_delete_payment() = runBlocking {
        val tipHistory = TipHistory(
            123456789,
            "",
            5,
            "",
            100.0,
            100.0,
            "",
            false
        )

        repository.addPayment(tipHistory)
        val paymentHistoryFirst = repository.paymentHistory()
        assertEquals(1, paymentHistoryFirst.size)


        repository.deletePayment(tipHistory)
        val paymentHistorySecond = repository.paymentHistory()
        assertEquals(0, paymentHistorySecond.size)
    }
}