package com.juanmaGutierrez.carcare.ui

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
import com.juanmaGutierrez.carcare.databinding.ActivityOnBoardingBinding
import com.juanmaGutierrez.carcare.model.OnBoardingData
import com.juanmaGutierrez.carcare.service.getView
import com.juanmaGutierrez.carcare.service.showSnackBar

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
        getView { view -> showSnackBar("entra en OnBoardingActivity", view) }
        tabLayout = binding.tabIndicator
        next = binding.tvNext
        val onBoardingData: MutableList<OnBoardingData> = getOnBoardingDataValues()
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
                    next!!.text = "Get Started"
                } else {
                    next!!.text = "Next"
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
                val activity = Intent(applicationContext, VehiclesActivity::class.java)
                startActivity(activity)
            }
        }
    }

    fun savePrefData() {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putBoolean(
            "isFirstTimeRun",
            true
        ) // TODO Modificar a false para no mostrar onBoarding
        editor.apply()
    }

    private fun getOnBoardingDataValues(): MutableList<OnBoardingData> {
        val onBoardingData: MutableList<OnBoardingData> = ArrayList()
        onBoardingData.add(
            OnBoardingData(
                "CarCare",
                "Carcare es tu aplicación para la gestión de gastos de tus vehículos",
                R.drawable.indicator_selector
            )
        )
        onBoardingData.add(
            OnBoardingData(
                "Vehículos",
                "Da de alta todos los vehículos que tengas, también puedes gestionar los que has tenido anteriormente.",
                R.drawable.indicator_selector
            )
        )
        onBoardingData.add(
            OnBoardingData(
                "Proveedores",
                "Agrega los proveedores que te prestan servicio.",
                R.drawable.indicator_selector
            )
        )
        onBoardingData.add(
            OnBoardingData(
                "Gastos",
                "Añade los gastos que surjan en cada momento.",
                R.drawable.indicator_selector
            )
        )
        return onBoardingData
    }

    private fun setOnBoardingViewPagerAdapter(onBoardingData: List<OnBoardingData>) {
        onBoardingViewPager = findViewById(R.id.screenPager)
        onBoardingViewPagerAdapter = OnBoardingViewPagerAdapter(this, onBoardingData)
        onBoardingViewPager!!.adapter = onBoardingViewPagerAdapter
        tabLayout?.setupWithViewPager(onBoardingViewPager)
    }


}