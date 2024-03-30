package com.juanmaGutierrez.carcare.ui.listItemActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.ui.listItemActivities.viewModel.ItemListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ItemListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemListBinding
    private lateinit var vehicleBinding: FragmentVehiclesListBinding
    private lateinit var viewModel: ItemListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vehicleBinding = FragmentVehiclesListBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        configureViewModel()
        viewModel.toolbarTitle.observe(this) { title -> supportActionBar?.title = title }
        CoroutineScope(Dispatchers.Main).launch {
            initVehiclesFrag()
        }
        vehicleBinding.veSwSwitchAllVehicles.setOnCheckedChangeListener { _, _ -> viewModel.loadVehiclesFromRoom() }
    }

    private fun initVehiclesFrag() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.saveFBVehiclesToRoom()
            viewModel.initVehiclesFragment()
        }
    }


    private fun configureViewModel() {
        viewModel.initVehiclesEnvironment(this, binding, vehicleBinding)
        viewModel.setToolbar(getString(R.string.menu_vehicles), this)
        viewModel.setNavigationBottombar(findViewById(R.id.bottom_bar), this)
    }
}