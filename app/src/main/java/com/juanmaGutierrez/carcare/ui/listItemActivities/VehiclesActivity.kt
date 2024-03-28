package com.juanmaGutierrez.carcare.ui.listItemActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.ActivityVehiclesBinding
import com.juanmaGutierrez.carcare.localData.AppDatabase
import com.juanmaGutierrez.carcare.localData.VehicleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class VehiclesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehiclesBinding
    private lateinit var viewModel: ListItemsViewModel
    private lateinit var adapter: VehicleAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehiclesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ListItemsViewModel::class.java]
        viewModel.setToolbar(getString(R.string.menu_vehicles), this)
        viewModel.setNavigationBottombar(findViewById(R.id.bottom_bar), this)
        viewModel.getVehiclesFromUser()

        adapter = VehicleAdapter(emptyList())
        binding.veRvVehicles.layoutManager = LinearLayoutManager(this)
        binding.veRvVehicles.adapter = adapter
        loadVehiclesFromRoom()
        binding.veSwSwitchAllVehicles.setOnCheckedChangeListener { _, _ -> loadVehiclesFromRoom() }
    }

    private fun loadVehiclesFromRoom() {
        val appDatabase = AppDatabase.getInstance(applicationContext)
        val vehicleDao = appDatabase.vehicleDao()
        GlobalScope.launch(Dispatchers.Main) {
            val vehicles = vehicleDao.getVehicles()
            val vehiclesFiltered = checkAvailablesVehicles(vehicles)
            adapter.updateData(vehiclesFiltered)
        }
    }

    private fun checkAvailablesVehicles(vehicles: List<VehicleEntity>): List<VehicleEntity> {
        if (binding.veSwSwitchAllVehicles.isChecked) {
            return vehicles
        }
        return vehicles.filter { it.available }
    }
}