package com.juanmaGutierrez.carcare.ui.vehicles

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityVehiclesBinding
import com.juanmaGutierrez.carcare.ui.viewModels.ListItemsViewModel


class VehiclesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehiclesBinding
    private lateinit var viewModel: ListItemsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehiclesBinding.inflate(layoutInflater)

        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ListItemsViewModel::class.java]
        viewModel.setToolbar(getString(R.string.menu_vehicles), this)
        viewModel.setBottombar(findViewById(R.id.bottom_bar), this)

    }

}
