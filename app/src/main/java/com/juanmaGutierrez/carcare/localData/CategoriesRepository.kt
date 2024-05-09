package com.juanmaGutierrez.carcare.localData

import android.app.Activity
import com.juanmaGutierrez.carcare.R

fun getVehicleCategories(activity: Activity): List<String> {
    return listOf(
        activity.getString(R.string.vehicle_category_car),
        activity.getString(R.string.vehicle_category_motorcycle),
        activity.getString(R.string.vehicle_category_van),
        activity.getString(R.string.vehicle_category_truck)
    )
}

fun getProviderCategories(activity: Activity): List<String> {
    return listOf(
        activity.getString(R.string.provider_category_gasStation),
        activity.getString(R.string.provider_category_workshop),
        activity.getString(R.string.provider_category_insuranceCompany),
        activity.getString(R.string.provider_category_towTruck),
        activity.getString(R.string.provider_category_ITV),
        activity.getString(R.string.provider_category_other)
    )
}