package com.example.tip_jar.di

import android.content.Context
import androidx.room.Room
import com.example.tip_jar.database.TipDatabase
import com.example.tip_jar.database.dao.TipHistoryDao
import com.example.tip_jar.di.repository.SavedPaymentsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun providesTipDatabase(
        @ApplicationContext context: Context
    ): TipDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TipDatabase::class.java,
            "tip_history_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesTipHistoryDao(
        tipDatabase: TipDatabase
    ) : TipHistoryDao {
        return tipDatabase.tipHistoryDao()
    }

    @Singleton
    @Provides
    fun providesSavedPaymentsRepository(
        tipHistoryDao: TipHistoryDao
    ) : SavedPaymentsRepository {
        return SavedPaymentsRepository(tipHistoryDao)
    }


}