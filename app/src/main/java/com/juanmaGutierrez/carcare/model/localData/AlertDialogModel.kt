package com.juanmaGutierrez.carcare.model.localData

import android.app.Activity
import android.graphics.drawable.Drawable

data class AlertDialogModel(
    val activity: Activity,
    val title: String,
    val message: String,
    val icon: Drawable?
)