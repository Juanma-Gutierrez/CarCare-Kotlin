package com.juanmaGutierrez.carcare.ui.mainActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.juanmaGutierrez.carcare.api.APIService
import com.juanmaGutierrez.carcare.ui.login.LoginActivity
import com.juanmaGutierrez.carcare.ui.onBoarding.OnBoardingActivity

class MainViewModel : ViewModel() {
    private lateinit var apiService: APIService
    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        if (!isFirstTimeRun(context)) {
            val activity = Intent(context, LoginActivity::class.java)
            context.startActivity(activity)
        } else {
            showOnBoardingActivity(context)
        }
    }

    private fun showOnBoardingActivity(context: Context) {
        val activity = Intent(context, OnBoardingActivity::class.java)
        context.startActivity(activity)
    }

    private fun isFirstTimeRun(context: Context): Boolean {
        sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("isFirstTimeRun", true)
    }
}
