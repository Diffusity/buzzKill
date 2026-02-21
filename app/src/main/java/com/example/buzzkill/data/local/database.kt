package com.example.buzzkill.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.buzzkill.data.local.dao.logsDao
import com.example.buzzkill.data.local.dao.rulesDao
import com.example.buzzkill.data.local.entity.logs
import com.example.buzzkill.data.local.entity.rules

@Database(
    entities = [
        rules::class,
        logs::class
    ],
    version = 1,
    exportSchema = true
)

abstract class database: RoomDatabase(){
    abstract fun rDao(): rulesDao
    abstract fun lDao(): logsDao
}