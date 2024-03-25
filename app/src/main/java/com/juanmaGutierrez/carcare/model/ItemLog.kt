package com.juanmaGutierrez.carcare.model

import java.time.LocalDateTime

enum class LogType {
    INFO, DEBUG, WARNING, ERROR, VERBOSE
}

data class ItemLog(
    val dateTime: LocalDateTime,
    val type: LogType,
    val currentUser: String? = "",
    val content: String,
    val amount: Number = 0.0,
    val vehicle: String = "",
)
