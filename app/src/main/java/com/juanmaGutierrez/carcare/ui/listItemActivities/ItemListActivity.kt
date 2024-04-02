package com.juanmaGutierrez.carcare.ui.listItemActivities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments.VehiclesListFragment
import com.juanmaGutierrez.carcare.ui.listItemActivities.viewModel.ItemListViewModel
import com.juanmaGutierrez.carcare.ui.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ItemListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemListBinding
    // private lateinit var vehicleBinding: FragmentVehiclesListBinding
    private lateinit var viewModel: ItemListViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        super.onCreate(savedInstanceState)
        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateToVehiclesFragment()
        configureViewModel()
        signOutAccepted()
        configureTopToolbar()

        /*      vehicleBinding = FragmentVehiclesListBinding.inflate(layoutInflater)
                configureViewModel()
                CoroutineScope(Dispatchers.Main).launch { initVehiclesFrag() }
                vehicleBinding.veSwSwitchAllVehicles.setOnCheckedChangeListener { _, _ -> viewModel.loadVehiclesFromRoom() }
         */
    }


    private fun navigateToVehiclesFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.itemList_fragment_container, VehiclesListFragment())
        fragmentTransaction.commit()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureTopToolbar() {
        viewModel.toolbarTitle.observe(this) { title -> supportActionBar?.title = title }
        binding.tbTopToolbar.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.tb_it_logout -> {
                    viewModel.setSignOutDialog()
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_top_app_bar, menu)
        return true
    }

    private fun configureViewModel() {
        Log.d("wanma", "Configure ViewModel")
        viewModel.initItemListEnvironment(this, binding)
        viewModel.setToolbar(getString(R.string.menu_vehicles), this)
        viewModel.setNavigationBottombar(findViewById(R.id.bottomBar), this)
        /*
                viewModel.initVehiclesEnvironment(this, binding, vehicleBinding)
        */
    }

/*    private fun initVehiclesFrag() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.saveFBVehiclesToRoom()
            viewModel.initVehiclesFragment()
        }
    }*/

    private fun signOutAccepted() {
        viewModel.signOut.observe(this) { isSignedOut ->
            if (isSignedOut) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}