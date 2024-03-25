package com.juanmaGutierrez.carcare.model

import com.juanmaGutierrez.carcare.service.LogType
import java.time.LocalDateTime

data class Log(
    val dateTime: LocalDateTime,
    val type: LogType,
    val content: String
)
