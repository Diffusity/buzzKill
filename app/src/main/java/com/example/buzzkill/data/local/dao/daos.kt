package com.example.buzzkill.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.buzzkill.data.local.entity.logs
import com.example.buzzkill.data.local.entity.rules
import kotlinx.coroutines.flow.Flow

@Dao
interface rulesDao{

    @Query("SELECT * from rules ORDER BY createdAt DESC")
    fun getAllRules(): Flow<List<rules>>

    @Query("SELECT * from rules WHERE isEnabled = 1")
    suspend fun getEnabledRules(): List<rules>

    @Query("SELECT * from rules WHERE id = :id")
    suspend fun getRulesByID(id: Long): rules?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rule: rules): Long

    @Update
    suspend fun updateRules(rule: rules)

    @Delete
    suspend fun deleteRules(rule: rules)

    @Query("UPDATE rules SET isEnabled = :enabled WHERE id = :id")
    suspend fun setRuleEnabled(id: Long , enabled: Boolean)

    @Query("DELETE from rules")
    suspend fun deleteAllRules()

}

@Dao
interface logsDao{

    @Query("SELECT * FROM logs ORDER BY receivedAt DESC")
    fun getAllLogs(): Flow<List<logs>>

    @Query("SELECT * from logs WHERE pkgName = :pkg ORDER BY receivedAt DESC")
    fun getLogsByPkg(pkg: String): Flow<List<logs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(log: logs): Long

    @Query("""
        DELETE from logs WHERE id NOT IN (
            SELECT id from logs ORDER BY receivedAt DESC LIMIT:keepCount
        )
    """)
    suspend fun trimOldLogs(keepCount: Int = 500)

    @Query("DELETE from logs")
    suspend fun deleteAllLogs()

}