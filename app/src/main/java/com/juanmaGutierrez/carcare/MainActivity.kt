package com.juanmaGutierrez.carcare

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.juanmaGutierrez.carcare.databinding.ActivityMainBinding
import com.juanmaGutierrez.carcare.services.showSnackBar

class MainActivity : AppCompatActivity() {

    var showOnBoarding = true
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (showOnBoarding){
            showOnBoardingActivity()
        }
/*
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

 */
    }

    private fun showOnBoardingActivity() {
        showSnackBar("entra en showOnBoardingActivity", binding.root)
    }
}