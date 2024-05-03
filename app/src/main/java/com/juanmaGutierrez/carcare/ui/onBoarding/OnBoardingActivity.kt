package com.juanmaGutierrez.carcare.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.OnBoardingViewPagerAdapter
import com.juanmaGutierrez.carcare.databinding.ActivityOnBoardingBinding
import com.juanmaGutierrez.carcare.localData.getOnBoardingDataValues
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.OnBoardingData
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.ui.login.LoginActivity

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private var onBoardingViewPagerAdapter: OnBoardingViewPagerAdapter? = null
    private var tabLayout: TabLayout? = null
    private var onBoardingViewPager: ViewPager? = null
    private var next: TextView? = null
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tabLayout = binding.tabIndicator
        next = binding.tvNext
        val onBoardingData: List<OnBoardingData> = getOnBoardingDataValues(this)
        setOnBoardingViewPagerAdapter(onBoardingData)
        position = onBoardingViewPager!!.currentItem
        setNextButton(onBoardingData)
        setTabDots(onBoardingData)
    }

    private fun setTabDots(onBoardingData: List<OnBoardingData>) {
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                if (tab.position == onBoardingData.size - 1) {
                    next!!.text = getString(R.string.get_started_button)
                } else {
                    next!!.text = getString(R.string.next_button)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                print(tab?.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                print(tab?.position)
            }
        })
    }

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

    private fun savePrefData() {
        ConfigService().savePrefDataBool(applicationContext, Constants.SETTINGS_IS_FIRST_TIME_RUN, false)
    }

    private fun setOnBoardingViewPagerAdapter(onBoardingData: List<OnBoardingData>) {
        onBoardingViewPager = findViewById(R.id.screenPager)
        onBoardingViewPagerAdapter = OnBoardingViewPagerAdapter(this, onBoardingData)
        onBoardingViewPager!!.adapter = onBoardingViewPagerAdapter
        tabLayout?.setupWithViewPager(onBoardingViewPager)
    }
}