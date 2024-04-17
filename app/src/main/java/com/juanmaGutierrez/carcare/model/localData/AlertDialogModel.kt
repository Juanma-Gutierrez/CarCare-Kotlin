package com.juanmaGutierrez.carcare.model.localData

import android.app.Activity

data class AlertDialogModel(
    val activity: Activity,
    val title: String,
    val message: String
)