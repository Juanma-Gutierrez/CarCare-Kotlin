package com.juanmaGutierrez.carcare.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.OnBoardingViewPagerAdapter
import com.juanmaGutierrez.carcare.databinding.ActivityOnBoardingBinding
import com.juanmaGutierrez.carcare.localData.getOnBoardingDataValues
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.OnBoardingData
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.ui.login.LoginActivity

/**
 * Activity responsible for displaying onboarding screens to the user.
 */
class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private var onBoardingViewPagerAdapter: OnBoardingViewPagerAdapter? = null
    private var onBoardingViewPager: ViewPager? = null
    private var next: TextView? = null
    private var position = 0

    /**
     * Called when the activity is starting.
     * Initializes the layout and sets up the onboarding screens.
     * @param savedInstanceState If non-null, this activity is being re-constructed from a previous saved state as given here.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        next = binding.tvNext
        val onBoardingData: List<OnBoardingData> = getOnBoardingDataValues(this)
        setOnBoardingViewPagerAdapter(onBoardingData)
        position = onBoardingViewPager!!.currentItem
        setNextButton(onBoardingData)
    }

    /**
     * Sets up the functionality of the 'Next' button.
     * Navigates to the next onboarding screen or the login screen if there are no more screens.
     * @param onBoardingData The list of onboarding data.
     */
    private fun setNextButton(onBoardingData: List<OnBoardingData>) {
        next?.setOnClickListener {
            if (position < onBoardingData.size) {
                position++
                onBoardingViewPager!!.currentItem = position
            }
            if (position == onBoardingData.size) {
                savePrefData()
                val activity = Intent(applicationContext, LoginActivity::class.java)
                startActivity(activity)
            }
        }
    }

    /**
     * Saves preference data after completing the onboarding process.
     */
    private fun savePrefData() {
        ConfigService().savePreferencesData(applicationContext, Constants.SETTINGS_IS_FIRST_TIME_RUN, false)
        ConfigService().savePreferencesData(applicationContext, Constants.SETTINGS_PROVIDERS_CHART_SIZE, "3.0")
    }

    /**
     * Sets up the adapter for the ViewPager to display onboarding screens.
     * @param onBoardingData The list of onboarding data.
     */
    private fun setOnBoardingViewPagerAdapter(onBoardingData: List<OnBoardingData>) {
        onBoardingViewPager = findViewById(R.id.screenPager)
        onBoardingViewPagerAdapter = OnBoardingViewPagerAdapter(this, onBoardingData)
        onBoardingViewPager!!.adapter = onBoardingViewPagerAdapter
    }
}