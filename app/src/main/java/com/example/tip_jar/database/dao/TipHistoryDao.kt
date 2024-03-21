package com.example.tip_jar.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tip_jar.database.entity.TipHistory

@Dao
interface TipHistoryDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPayment(tipHistory : TipHistory)

    @Query("SELECT * FROM tip_history ORDER BY timestamp DESC")
    suspend fun getPaymentHistory() : List<TipHistory>

    @Delete
    suspend fun deletePayment(tipHistory : TipHistory)
}