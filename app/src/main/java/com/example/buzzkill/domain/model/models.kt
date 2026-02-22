package com.example.buzzkill.domain.model

enum class ruleActions(val label: String){
    COOLDOWN("coolDown"),
    DISMISS("dismiss"),
    REMIND("remind"),
    SNOOZE("snooze"),
    SPEAK("speak"),
    CUSTOM_SOUND("customSound"),
    VIBRATE("customVibrate"),
    SILENT("silent"),
//    AUTO_REPLY("autoReply"),
    NONE("passThrough")
}

data class notificationRules(
    val id: Long = 0,
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

data class notificationLogs(
    val id: Long = 0,
    val pkgName: String,
    val title: String?,
    val body: String?,
    val appliedRules: String?,
    val action: String?,
    val receivedAt: Long = System.currentTimeMillis(),
    val wasDismissed: Boolean = false
)