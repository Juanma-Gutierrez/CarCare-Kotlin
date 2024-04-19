package com.juanmaGutierrez.carcare.model.localData

enum class LogType {
    INFO, DEBUG, WARNING, ERROR, VERBOSE
}

enum class OperationLog {
    CREATEUSER, LOGIN, LOGOUT, CREATEVEHICLE, CREATEPROVIDER, CREATESPENT
}

data class ItemLog(
    val dateTime: String,
    val type: LogType,
    val operationLog: OperationLog,
    val currentUser: String? = "",
    val uid: String? = "",
    val content: String,
)
