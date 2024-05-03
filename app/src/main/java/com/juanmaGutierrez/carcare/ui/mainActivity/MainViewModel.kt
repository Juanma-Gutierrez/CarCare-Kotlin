package com.juanmaGutierrez.carcare.ui.mainActivity

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.ui.login.LoginActivity
import com.juanmaGutierrez.carcare.ui.onBoarding.OnBoardingActivity

class MainViewModel : ViewModel() {

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
        return ConfigService().getPreferencesBoolean(context, Constants.SETTINGS_IS_FIRST_TIME_RUN)
    }
}
