package com.juanmaGutierrez.carcare.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.juanmaGutierrez.carcare.model.Log
import java.time.LocalDateTime

enum class LogType {
    INFO, DEBUG, WARNING, ERROR, VERBOSE
}

@RequiresApi(Build.VERSION_CODES.O)
fun fbSaveLog(type: LogType, message: String) {
    System.out.println("DENTRO DE FBSAVELOG")
    System.out.println("Tipo: $type Mensaje: $message")
    val itemLog: Log = Log(LocalDateTime.now(), type, message)
    System.out.println(itemLog)
}



