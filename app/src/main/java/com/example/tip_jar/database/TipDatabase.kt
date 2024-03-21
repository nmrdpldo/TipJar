package com.example.tip_jar.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tip_jar.database.dao.TipHistoryDao
import com.example.tip_jar.database.entity.TipHistory

@Database(
    entities = [TipHistory::class],
    version = 1,
    exportSchema = false)
abstract class TipDatabase : RoomDatabase() {
    abstract fun tipHistoryDao(): TipHistoryDao
}
