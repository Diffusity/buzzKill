package com.example.buzzkill.domain.usecase

import com.example.buzzkill.data.local.entity.logs
import com.example.buzzkill.data.repos.logsRepo
import com.example.buzzkill.data.repos.ruleRepo
import com.example.buzzkill.domain.model.notificationLogs
import com.example.buzzkill.domain.model.notificationRules
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class getALlRulesUseCase @Inject constructor(private val repo: ruleRepo){
    operator fun invoke(): Flow<List<notificationRules>> = repo.allRules
}

class saveRuleUseCase @Inject constructor(private val repo: ruleRepo){
    suspend operator fun invoke(rule: notificationRules): Long = repo.saveRule(rule)
}

class deleteRuleUseCase @Inject constructor(private val repo: ruleRepo){
    suspend operator fun invoke(rule: notificationRules) = repo.deleteRule(rule)
}

class toggleRuleUseCase @Inject constructor(private val repo: ruleRepo){
    suspend operator fun invoke(id: Long , enabled: Boolean) = repo.setRuleEnabled(id , enabled)
}

class matchRuleUseCase @Inject constructor(private val repo: ruleRepo){
    suspend operator fun invoke(pkgname: String , title: String? , body: String?): notificationRules? {
        val enabledRules = repo.getEnabledRules()
        val notificationText = "${title.orEmpty()} ${body.orEmpty()}".lowercase()

        return enabledRules.firstOrNull() {rule ->
            val pkgMatch = rule.targetPkg == null || rule.targetPkg == pkgname
            val keywordMatch = rule.keywordFilter == null || notificationText.contains(rule.keywordFilter)
            val timeMatch = rule.isWithinTimeWindow()

            pkgMatch && keywordMatch && timeMatch
        }
    }

    private fun notificationRules.isWithinTimeWindow(): Boolean{
        if(timeStart == null || timeEnd == null)
            return true

        val now = java.util.Calendar.getInstance()
        val nowMin = now.get(java.util.Calendar.HOUR_OF_DAY) * 60 + now.get(java.util.Calendar.MINUTE)

        val (startH , startM) = timeStart.split(":").map{it.toInt()}
        val (endH , endM) = timeEnd.split(":").map{it.toInt()}
        val startMin = startH * 60 + startM
        val endMin = endH * 60 + endM

        return if(startMin <= endMin){
            nowMin in startMin..endMin
        }else{
            nowMin >= startMin || nowMin <= endMin
        }
    }
}

class getNotificationLogsUseCase @Inject constructor(private val repo: logsRepo){
    operator fun invoke(): Flow<List<notificationLogs>> = repo.allLogs
}

class addNotificationLogUseCase @Inject constructor(private val repo: logsRepo){
    suspend operator fun invoke(log: notificationLogs) = repo.addLog(log)
}

class clearLogsUseCase @Inject constructor(private val repo: logsRepo){
    suspend operator fun invoke() = repo.clearAllLogs()
}