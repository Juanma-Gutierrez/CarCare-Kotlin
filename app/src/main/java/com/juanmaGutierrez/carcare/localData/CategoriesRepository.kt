package com.juanmaGutierrez.carcare.localData

import android.app.Activity
import com.juanmaGutierrez.carcare.R

fun getCategories(activity: Activity): List<String> {
    return listOf(
        activity.getString(R.string.vehicle_category_car),
        activity.getString(R.string.vehicle_category_motorcycle),
        activity.getString(R.string.vehicle_category_van),
        activity.getString(R.string.vehicle_category_truck)
    )
}

