package com.juanmaGutierrez.carcare.model.localData

import java.time.LocalDateTime

enum class LogType {
    INFO, DEBUG, WARNING, ERROR, VERBOSE
}

enum class OperationLog {
    CREATEUSER, LOGIN, LOGOUT, CREATEVEHICLE, CREATEPROVIDER, CREATESPENT
}

data class ItemLog(
    val dateTime: LocalDateTime,
    val type: LogType,
    val operationLog: OperationLog,
    val currentUser: String? = "",
    val uid: String? = "",
    val content: String,
)
