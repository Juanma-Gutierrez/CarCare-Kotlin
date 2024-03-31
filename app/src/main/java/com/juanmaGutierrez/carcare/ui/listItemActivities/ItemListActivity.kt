package com.juanmaGutierrez.carcare.ui.listItemActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.ui.listItemActivities.viewModel.ItemListViewModel
import com.juanmaGutierrez.carcare.ui.login.LoginActivity
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

        viewModel.signOut.observe(this) { isSignedOut ->
            if (isSignedOut) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun configureTopToolbar() {
        Log.d("wanma", "Configure toptoolbar")
        viewModel.toolbarTitle.observe(this) { title -> supportActionBar?.title = title }
        val topAppBar = binding.tbTopToolbar.topAppBar
        topAppBar.setNavigationOnClickListener {
            viewModel.setSignOutDialog()
        }
    }

    private fun initVehiclesFrag() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.saveFBVehiclesToRoom()
            viewModel.initVehiclesFragment()
        }
    }

    private fun configureViewModel() {
        Log.d("wanma", "Configure ViewModel")
        viewModel.initVehiclesEnvironment(this, binding, vehicleBinding)
        viewModel.setToolbar(getString(R.string.menu_vehicles), this)
        viewModel.setNavigationBottombar(findViewById(R.id.bottomBar), this)
    }
}