package com.example.buzzkill.data.repos

import com.example.buzzkill.data.local.dao.logsDao
import com.example.buzzkill.data.local.dao.rulesDao
import com.example.buzzkill.data.local.entity.logs
import com.example.buzzkill.data.local.entity.rules
import com.example.buzzkill.domain.model.notificationLogs
import com.example.buzzkill.domain.model.notificationRules
import com.example.buzzkill.domain.model.ruleActions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ruleRepo @Inject constructor(private val rule: rulesDao){
    val allRules: Flow<List<notificationRules>> = rule.getAllRules().map{
        list -> list.map{it.toDomain()}
    }

    suspend fun getEnabledRules(): List<notificationRules> =
        rule.getEnabledRules().map {it.toDomain()}

    suspend fun getRuleById(id: Long): notificationRules? =
        rule.getRulesByID(id)?.toDomain()

    suspend fun saveRule(rules: notificationRules): Long =
        rule.insertRules(rules.toEntity())

    suspend fun updateRule(rules: notificationRules) =
        rule.updateRules(rules.toEntity())

    suspend fun deleteRule(rules: notificationRules) =
        rule.deleteRules(rules.toEntity())

    suspend fun setRuleEnabled(id: Long, enabled: Boolean) =
        rule.setRuleEnabled(id, enabled)
}

@Singleton
class logsRepo @Inject constructor(private val log: logsDao){
    val allLogs: Flow<List<notificationLogs>> =
        log.getAllLogs().map{list -> list.map {it.toDomain()}}

    fun getLogsByPackage(pkg: String): Flow<List<notificationLogs>> =
        log.getLogsByPkg(pkg).map{list -> list.map{it.toDomain()}}

    suspend fun addLog(logs: notificationLogs) {
        log.insertLogs(logs.toEntity())
        log.trimOldLogs(keepCount = 500)
    }

    suspend fun clearAllLogs() = log.deleteAllLogs()
}

private fun rules.toDomain() = notificationRules(
    id = id,
    name = name,
    targetPkg = targetPkg,
    keywordFilter = keywordFilter,
    action = action,
    timeStart = timeStart,
    timeEnd = timeEnd,
    coolDownTime = coolDownTime,
    isEnabled = isEnabled,
    createdAt = createdAt
)


private fun notificationRules.toEntity() = rules(
    id = id,
    name = name,
    targetPkg = targetPkg,
    keywordFilter = keywordFilter,
    action = action,
    timeStart = timeStart,
    timeEnd = timeEnd,
    coolDownTime = coolDownTime,
    isEnabled = isEnabled,
    createdAt = createdAt
)

private fun logs.toDomain() = notificationLogs(
    id = id,
    pkgName = pkgName,
    title = title,
    body = body,
    appliedRules = appliedRules,
    action = action,
    receivedAt = receivedAt,
    wasDismissed = wasDismissed
)

private fun notificationLogs.toEntity() = logs(
    id = id,
    pkgName = pkgName,
    title = title,
    body = body,
    appliedRules = appliedRules,
    action = action,
    receivedAt = receivedAt,
    wasDismissed = wasDismissed
)