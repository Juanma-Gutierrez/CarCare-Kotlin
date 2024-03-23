package com.juanmaGutierrez.carcare.ui.mainActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.juanmaGutierrez.carcare.ui.login.LoginActivity
import com.juanmaGutierrez.carcare.ui.onBoarding.OnBoardingActivity

class MainActivityViewModel(private val context: Context) : ViewModel() {
    private var sharedPreferences: SharedPreferences? = null

    init {
        print("entra en viewmodel");

        System.out.println(isFirstTimeRun())
        if (!isFirstTimeRun()) {
            val activity = Intent(context, LoginActivity::class.java)
            context.startActivity(activity)
            // finish()
        } else {
            showOnBoardingActivity()
        }
    }

    private fun showOnBoardingActivity() {
        val activity = Intent(context, OnBoardingActivity::class.java)
        context.startActivity(activity)
        //finish()
    }

    private fun isFirstTimeRun(): Boolean {
        sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("isFirstTimeRun", true)
    }
}