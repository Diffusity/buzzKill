package com.example.buzzkill.di

import android.content.Context
import androidx.room.Room
import com.example.buzzkill.data.local.dao.logsDao
import com.example.buzzkill.data.local.dao.rulesDao
import com.example.buzzkill.data.local.database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{
    @Provides
    @Singleton
    fun provideDatabse(@ApplicationContext context: Context): database = Room.databaseBuilder(
        context,
        database::class.java,
        "buzzKill.db"
    ).fallbackToDestructiveMigration().build()
}

@Provides
@Singleton
fun provideRulesDao(db: database): rulesDao = db.rDao()

@Provides
@Singleton
fun provideLogsDao(db: database): logsDao = db.lDao()