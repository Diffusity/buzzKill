package com.example.buzzkill.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rules")
data class rules(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetPkg: String?,
    val keywordFilter: String?,
    val action: String,
    val timeStart: String?,
    val timeEnd: String?,
    val coolDownTime: Int = 5,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "logs")
data class logs(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pkgName: String,
    val title: String?,
    val body: String?,
    val appliedRules: String?,
    val action: String?,
    val receivedAt: Long = System.currentTimeMillis(),
    val wasDismissed: Boolean = false
)