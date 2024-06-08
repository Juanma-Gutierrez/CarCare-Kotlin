package com.juanmaGutierrez.carcare.ui.mainActivity

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.ui.login.LoginActivity
import com.juanmaGutierrez.carcare.ui.onBoarding.OnBoardingActivity

/**
 * ViewModel for the main functionality of the application.
 */
class MainViewModel : ViewModel() {

    /**
     * Initializes the main functionality of the application.
     * If it's the first time the app runs, it starts the onboarding process.
     * Otherwise, it navigates to the login screen.
     * @param context The context of the application.
     */
    fun init(context: Context) {
        if (!isFirstTimeRun(context)) {
            val activity = Intent(context, LoginActivity::class.java)
            context.startActivity(activity)
        } else {
            ConfigService().savePreferencesData(context, Constants.SETTINGS_IS_FIRST_TIME_RUN, false)
            showOnBoardingActivity(context)
        }
    }

    /**
     * Shows the onboarding activity.
     * @param context The context of the application.
     */
    private fun showOnBoardingActivity(context: Context) {
        val activity = Intent(context, OnBoardingActivity::class.java)
        context.startActivity(activity)
    }

    /**
     * Checks if it's the first time the app runs.
     * @param context The context of the application.
     * @return True if it's the first time the app runs, false otherwise.
     */
    private fun isFirstTimeRun(context: Context): Boolean {
        return ConfigService().getPreferencesBoolean(context, Constants.SETTINGS_IS_FIRST_TIME_RUN)
    }
}
