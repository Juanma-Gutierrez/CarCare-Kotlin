package com.juanmaGutierrez.carcare.ui.vehicles

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityVehiclesBinding


class VehiclesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehiclesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehiclesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.tb_toolbar))
        supportActionBar?.title = "Vehículos"
    }
}
