package com.example.buzzkill.di

import android.content.Context
import androidx.room.Room
import androidx.work.impl.Migration_1_2
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
    ).addMigrations(Migration_1_2).build()


    @Provides
    @Singleton
    fun provideRulesDao(db: database): rulesDao = db.rDao()

    @Provides
    @Singleton
    fun provideLogsDao(db: database): logsDao = db.lDao()
}

val Migration_1_2 = object : androidx.room.migration.Migration(1, 2) {
    override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE rules ADD COLUMN predefinedReply TEXT"
        )
    }
}