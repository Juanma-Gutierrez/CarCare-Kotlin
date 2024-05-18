package com.juanmaGutierrez.carcare.model.localData

enum class LogType {
    INFO, DEBUG, WARNING, ERROR, VERBOSE
}

enum class OperationLog {
    CREATE_USER, LOGIN, LOGOUT, VEHICLE, PROVIDER, SPENT
}

data class ItemLog(
    val dateTime: String,
    val type: LogType,
    val operationLog: OperationLog,
    val currentUser: String? = "",
    val uid: String? = "",
    val content: String,
){
    override fun toString(): String {
        return "DateTime: $dateTime\nType: $type\nOperationLog: $operationLog\nCurrentUser: $currentUser\nUid: $uid\nContent: $content"
    }
}
