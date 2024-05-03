package com.juanmaGutierrez.carcare.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.juanmaGutierrez.carcare.model.Constants.Companion.TAG

class ConfigService {
    private var sharedPreferences: SharedPreferences? = null

    companion object {
        var vehicleListFormatDetail = false
    }

    fun savePrefDataBool(context: Context, key: String, value: Any) {
        sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            else -> Log.e(TAG, "Error in SharedPreferences")
        }
        editor.apply()
    }

    fun getPreferencesBoolean(context: Context, key: String): Boolean {
        val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, true)
    }

    fun getPreferencesString(context: Context, key: String): String {
        val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "") ?: ""
    }
}