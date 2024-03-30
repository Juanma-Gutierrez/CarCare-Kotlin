package com.juanmaGutierrez.carcare.ui.listItemActivities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.service.showSnackBar
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
        vehicleBinding = FragmentVehiclesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        configureViewModel()
        configureTopToolbar()
        CoroutineScope(Dispatchers.Main).launch { initVehiclesFrag() }
        vehicleBinding.veSwSwitchAllVehicles.setOnCheckedChangeListener { _, _ -> viewModel.loadVehiclesFromRoom() }
    }

    private fun configureTopToolbar() {
        viewModel.toolbarTitle.observe(this) { title -> supportActionBar?.title = title }
        val topAppBar = binding.tbTopToolbar.topAppBar
        topAppBar.setNavigationOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.logout_title))
                .setMessage(resources.getString(R.string.logout_message))
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    showSnackBar(resources.getString(R.string.cancel_message), findViewById(android.R.id.content))
                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    Log.d("wanma", "Aceptar")
                }
                .show()
        }
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
        viewModel.setNavigationBottombar(findViewById(R.id.bottomBar), this)
    }
}