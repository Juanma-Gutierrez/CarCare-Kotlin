package com.juanmaGutierrez.carcare.model.localData

/**
 * Enum representing the type of log.
 */
enum class LogType {
    INFO, ERROR
}

/**
 * Enum representing various operations for logging.
 */
enum class OperationLog {
    CREATE_USER, LOGIN, LOGOUT, VEHICLE, PROVIDER, SPENT
}

/**
 * Represents a log item.
 * @property dateTime The date and time of the log.
 * @property type The type of the log (INFO or ERROR).
 * @property operationLog The operation associated with the log.
 * @property currentUser The current user associated with the log.
 * @property uid The unique identifier associated with the log.
 * @property content The content of the log.
 */
data class ItemLog(
    val dateTime: String,
    val type: LogType,
    val operationLog: OperationLog,
    val currentUser: String? = "",
    val uid: String? = "",
    val content: String,
) {
    /**
     * Returns a string representation of the log item.
     */
    override fun toString(): String {
        return "DateTime: $dateTime\nType: $type\nOperationLog: $operationLog\nCurrentUser: $currentUser\nUid: $uid\nContent: $content"
    }
}
