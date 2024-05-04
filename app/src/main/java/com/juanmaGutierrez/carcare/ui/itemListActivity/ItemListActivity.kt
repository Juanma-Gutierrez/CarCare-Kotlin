package com.juanmaGutierrez.carcare.ui.itemListActivity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.service.fbGetUserLogged
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.providersList.ProvidersListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList.SpentsListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList.VehiclesListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.viewModel.ItemListViewModel
import com.juanmaGutierrez.carcare.ui.login.LoginActivity


class ItemListActivity : AppCompatActivity(), ItemListViewModel.NavigationListener {
    private lateinit var binding: ActivityItemListBinding
    private lateinit var viewModel: ItemListViewModel
    private var alertDialog: AlertDialog? = null

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
        viewModel.openSettingsDialog.observe(this) { _ -> openSettingsDialog() }
    }

    private fun openSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val compactFormat =
            ConfigService().getPreferencesBoolean(this, Constants.SETTINGS_VEHICLES_LIST_COMPACT)
        when (compactFormat) {
            true -> dialogView.findViewById<RadioGroup>(R.id.ds_rg_vehicleListFormat)
                .check(R.id.ds_rb_vehicleList_compact)

            false -> dialogView.findViewById<RadioGroup>(R.id.ds_rg_vehicleListFormat)
                .check(R.id.ds_rb_vehicleList_detailed)
        }
        dialogView.findViewById<RadioGroup>(R.id.ds_rg_vehicleListFormat)
            .setOnCheckedChangeListener { _, checkedId ->
                val compact = when (checkedId) {
                    R.id.ds_rb_vehicleList_compact -> true
                    R.id.ds_rb_vehicleList_detailed -> false
                    else -> false
                }
                ConfigService().savePrefDataBool(this, Constants.SETTINGS_VEHICLES_LIST_COMPACT, compact)
                recreate()
            }
        dialogView.findViewById<TextView>(R.id.ds_tv_close).setOnClickListener {
            alertDialog?.dismiss()
        }
        alertDialog = MaterialAlertDialogBuilder(this).setView(dialogView).show()
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
        supportFragmentManager.beginTransaction().replace(R.id.itemList_fragment_container, fragment).commit()
    }

    private fun configureTopToolbar() {
        viewModel.toolbarTitle.observe(this) { title -> supportActionBar?.title = title }
        binding.tbTopToolbar.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.tb_it_settings -> {
                    viewModel.setSettingsDialog()
                    true
                }

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