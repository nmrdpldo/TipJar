package com.example.tip_jar.di.repository

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import com.example.tip_jar.database.dao.TipHistoryDao
import com.example.tip_jar.database.entity.TipHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SavedPaymentsRepository @Inject constructor(private val tipHistoryDao: TipHistoryDao){

    suspend fun paymentHistory(): List<TipHistory> {
        return tipHistoryDao.getPaymentHistory()
    }

    suspend fun addPayment(tipHistory: TipHistory) = withContext(Dispatchers.IO) {
        tipHistoryDao.addPayment(tipHistory)
    }

    suspend fun deletePayment(tipHistory: TipHistory) = withContext(Dispatchers.IO) {
        tipHistoryDao.deletePayment(tipHistory)
    }
}