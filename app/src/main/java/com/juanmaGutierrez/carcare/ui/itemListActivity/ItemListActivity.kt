package com.juanmaGutierrez.carcare.ui.itemListActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.service.fbGetUserLogged
import com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle.VehicleDetailFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList.VehiclesListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.viewModel.ItemListViewModel
import com.juanmaGutierrez.carcare.ui.login.LoginActivity


class ItemListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemListBinding
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
        val userEmail = fbGetUserLogged()?.email ?: ""
        val userEmailMenuItem = menu.findItem(R.id.tb_it_userEmail)
        userEmailMenuItem.title = userEmail
        return true
    }

    private fun configureViewModel() {
        viewModel.initItemListEnvironment(this, binding)
        viewModel.setToolbar(getString(R.string.menu_vehicles), this)
        viewModel.setNavigationBottombar(findViewById(R.id.bottomBar), this)
    }

    private fun signOutAccepted() {
        viewModel.signOut.observe(this) { isSignedOut ->
            if (isSignedOut) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}