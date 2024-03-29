package com.juanmaGutierrez.carcare.ui.listItemActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.localData.AppDatabase
import com.juanmaGutierrez.carcare.localData.VehicleEntity
import com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments.VehiclesListFragment
import com.juanmaGutierrez.carcare.ui.listItemActivities.viewModel.ItemListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ItemListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemListBinding
    private lateinit var vehicleBinding: FragmentVehiclesListBinding
    private lateinit var viewModel: ItemListViewModel
    private lateinit var vehicleAdapter: VehicleAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vehicleBinding = FragmentVehiclesListBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        viewModel.setToolbar(getString(R.string.menu_vehicles), this)
        viewModel.setNavigationBottombar(findViewById(R.id.bottom_bar), this)
        viewModel.getVehiclesFromUser()
        viewModel.toolbarTitle.observe(this) { title -> supportActionBar?.title = title }
        initVehiclesFragment()
        initVehicles()
    }

    private fun initVehicles() {
        vehicleAdapter = VehicleAdapter(emptyList())
        vehicleBinding.veRvVehicles.layoutManager = LinearLayoutManager(this)
        vehicleBinding.veRvVehicles.adapter = vehicleAdapter
        loadVehiclesFromRoom()
        vehicleBinding.veSwSwitchAllVehicles.setOnCheckedChangeListener { _, _ -> loadVehiclesFromRoom() }
    }

    private fun initVehiclesFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.itemListFragmentContainer.id, VehiclesListFragment())
        fragmentTransaction.commit()
    }

    private fun loadVehiclesFromRoom() {
        val appDatabase = AppDatabase.getInstance(applicationContext)
        val vehicleDao = appDatabase.vehicleDao()
        GlobalScope.launch(Dispatchers.Main) {
            val vehicles = vehicleDao.getVehicles()
            val vehiclesFiltered = checkAvailablesVehicles(vehicles)
            vehicleAdapter.updateData(vehiclesFiltered)
        }
    }

    private fun checkAvailablesVehicles(vehicles: List<VehicleEntity>): List<VehicleEntity> {
        if (vehicleBinding.veSwSwitchAllVehicles.isChecked) {
            return vehicles
        }
        return vehicles.filter { it.available }
    }
}