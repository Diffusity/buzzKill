package com.example.buzzkill.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.buzzkill.domain.model.notificationLogs
import com.example.buzzkill.domain.usecase.addNotificationLogUseCase
import com.example.buzzkill.domain.usecase.matchRuleUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class notificationListener: NotificationListenerService(){
    @Inject lateinit var matchRule: matchRuleUseCase
    @Inject lateinit var addLog: addNotificationLogUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onNotificationPosted(sbn: StatusBarNotification){
        val pkg = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE)
        val body = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()

        if(pkg == applicationContext.packageName){
            return
        }

        serviceScope.launch{
            val matchRule = matchRule(pkgname = pkg , title = title , body = body)

            if(matchRule != null){
                applyAction(sbn , matchRule.action)
            }

            addLog(
                notificationLogs(
                    pkgName = pkg,
                    title = title,
                    body = body,
                    appliedRules = matchRule?.name,
                    action = matchRule?.action,
                    wasDismissed = matchRule?.action == "DISMISS"
                )
            )
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun applyAction(sbn: StatusBarNotification , action: String){
        when(action){
            "DISMISS" -> cancelNotification(sbn.key)
            "SILENT" -> {}
            "COOLDOWN" -> {}
            "REMIND" -> {}
            "SPEAK" -> {}
            "SNOOZE" -> {}
            else -> {}
        }
    }
}