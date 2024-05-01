package com.juanmaGutierrez.carcare.ui.itemListActivity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.service.fbGetUserLogged
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.providersList.ProvidersListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList.SpentsListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList.VehiclesListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.viewModel.ItemListViewModel
import com.juanmaGutierrez.carcare.ui.login.LoginActivity


class ItemListActivity : AppCompatActivity(), ItemListViewModel.NavigationListener {
    private lateinit var binding: ActivityItemListBinding
    private lateinit var viewModel: ItemListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        super.onCreate(savedInstanceState)
        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openSelectedFragment()
        configureViewModel()
        signOutAccepted()
        configureTopToolbar()
        viewModel.setNavigationListener(this)
    }


    private fun openSelectedFragment() {
        val intent = intent
        val destinationFragment = intent.getStringExtra("destinationFragment")
        when (destinationFragment) {
            null, "vehiclesList" -> {
                viewModel.setToolbar(getString(R.string.menu_vehicles), this)
                binding.bbBottombar.bottomBar.selectedItemId = R.id.navigation_vehicles
                navigateToFragment(VehiclesListFragment())
            }

            "providersList" -> {
                viewModel.setToolbar(getString(R.string.menu_providers), this)
                binding.bbBottombar.bottomBar.selectedItemId = R.id.navigation_providers
                navigateToFragment(ProvidersListFragment())
            }

            "spentsList" -> {
                viewModel.setToolbar(getString(R.string.menu_spents), this)
                binding.bbBottombar.bottomBar.selectedItemId = R.id.navigation_spents
                navigateToFragment(SpentsListFragment())
            }
        }
    }


    override fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.itemList_fragment_container, fragment).commit()
    }

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