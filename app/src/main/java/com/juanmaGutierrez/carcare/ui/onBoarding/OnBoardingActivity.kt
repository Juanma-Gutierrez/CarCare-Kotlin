package com.juanmaGutierrez.carcare.ui.onBoarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.OnBoardingViewPagerAdapter
import com.juanmaGutierrez.carcare.data.getOnBoardingDataValues
import com.juanmaGutierrez.carcare.databinding.ActivityOnBoardingBinding
import com.juanmaGutierrez.carcare.model.OnBoardingData
import com.juanmaGutierrez.carcare.ui.login.LoginActivity
import com.juanmaGutierrez.carcare.ui.vehicles.VehiclesActivity

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private var onBoardingViewPagerAdapter: OnBoardingViewPagerAdapter? = null
    private var tabLayout: TabLayout? = null
    private var onBoardingViewPager: ViewPager? = null
    private var next: TextView? = null
    private var position = 0
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tabLayout = binding.tabIndicator
        next = binding.tvNext
        val onBoardingData: MutableList<OnBoardingData> = getOnBoardingDataValues(this)
        setOnBoardingViewPagerAdapter(onBoardingData)
        position = onBoardingViewPager!!.currentItem
        setNextButton(onBoardingData)
        setTabDots(onBoardingData)
    }

    private fun setTabDots(onBoardingData: MutableList<OnBoardingData>) {
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
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun setNextButton(onBoardingData: MutableList<OnBoardingData>) {
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

    fun savePrefData() {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putBoolean(
            "isFirstTimeRun",
            false
        ) // TODO Cambiar a -false- para no mostrar onBoarding
        editor.apply()
    }

    private fun setOnBoardingViewPagerAdapter(onBoardingData: List<OnBoardingData>) {
        onBoardingViewPager = findViewById(R.id.screenPager)
        onBoardingViewPagerAdapter = OnBoardingViewPagerAdapter(this, onBoardingData)
        onBoardingViewPager!!.adapter = onBoardingViewPagerAdapter
        tabLayout?.setupWithViewPager(onBoardingViewPager)
    }
}