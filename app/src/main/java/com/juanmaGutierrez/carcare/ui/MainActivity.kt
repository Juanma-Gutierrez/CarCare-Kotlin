package com.juanmaGutierrez.carcare.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juanmaGutierrez.carcare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!isFirstTimeRun()) {
            val activity = Intent(applicationContext, VehiclesActivity::class.java)
            startActivity(activity)
            finish()
        } else {
            showOnBoardingActivity()
        }
    }

    private fun showOnBoardingActivity() {
        val activity = Intent(applicationContext, OnBoardingActivity::class.java)
        startActivity(activity)
        finish()
    }

    private fun isFirstTimeRun(): Boolean {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("isFirstTimeRun", true)
    }
}