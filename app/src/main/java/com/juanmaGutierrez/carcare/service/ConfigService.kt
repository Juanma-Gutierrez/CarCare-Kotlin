package com.juanmaGutierrez.carcare.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.juanmaGutierrez.carcare.model.Constants.Companion.TAG

/**
 * Service class for managing application configuration data using SharedPreferences.
 */
class ConfigService {
    private var sharedPreferences: SharedPreferences? = null

    /**
     * Saves data to SharedPreferences.
     *
     * @param context The context of the application.
     * @param key The key under which the data will be saved.
     * @param value The value to be saved.
     */
    fun savePreferencesData(context: Context, key: String, value: Any) {
        sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            else -> Log.e(TAG, "Error in SharedPreferences")
        }
        editor.apply()
    }

    /**
     * Retrieves a boolean value from SharedPreferences.
     *
     * @param context The context of the application.
     * @param key The key to retrieve the boolean value.
     * @param defaultValue The default value if the key is not found.
     * @return The retrieved boolean value.
     */
    fun getPreferencesBoolean(context: Context, key: String, defaultValue: Boolean = true): Boolean {
        val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    /**
     * Retrieves a string value from SharedPreferences.
     *
     * @param context The context of the application.
     * @param key The key to retrieve the string value.
     * @return The retrieved string value, or an empty string if the key is not found.
     */
    fun getPreferencesString(context: Context, key: String): String {
        val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return if (sharedPreferences.contains(key)) {
            sharedPreferences.getString(key, "") ?: ""
        } else {
            ""
        }
    }
}