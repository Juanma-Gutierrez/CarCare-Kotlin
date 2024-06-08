package com.juanmaGutierrez.carcare.ui.itemListActivity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.service.fbGetUserLogged
import com.juanmaGutierrez.carcare.ui.detailActivity.fragment.aboutMe.AboutMeFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.providersList.ProvidersListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList.SpentsListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList.VehiclesListFragment
import com.juanmaGutierrez.carcare.ui.login.LoginActivity

/**
 * Activity for displaying and managing the item list environment.
 */
class ItemListActivity : AppCompatActivity(), ItemListViewModel.NavigationListener {
    private lateinit var binding: ActivityItemListBinding
    private lateinit var viewModel: ItemListViewModel
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openSelectedFragment()
        configureViewModel()
        signOutAccepted()
        configureTopToolbar()
        viewModel.setNavigationListener(this)
        viewModel.openSettingsDialog.observe(this) { _ -> openSettingsDialog() }
    }

    /**
     * Opens the settings dialog for configuring preferences.
     */
    private fun openSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        configureVehiclesListFormat(dialogView)
        configureProvidersListFormat(dialogView)
        configureProvidersChartSize(dialogView)
        configureCloseButton(dialogView)
        alertDialog = MaterialAlertDialogBuilder(this).setView(dialogView).show()
    }

    /**
     * Configures the format of the vehicles list in the settings dialog.
     *
     * @param dialogView The view of the settings dialog.
     */
    private fun configureVehiclesListFormat(dialogView: View) {
        val compactFormat = ConfigService().getPreferencesBoolean(this, Constants.SETTINGS_VEHICLES_LIST_COMPACT)
        when (compactFormat) {
            true -> dialogView.findViewById<RadioGroup>(R.id.ds_rg_vehicleListFormat)
                .check(R.id.ds_rb_vehicleList_compact)

            false -> dialogView.findViewById<RadioGroup>(R.id.ds_rg_vehicleListFormat)
                .check(R.id.ds_rb_vehicleList_detailed)
        }
        dialogView.findViewById<RadioGroup>(R.id.ds_rg_vehicleListFormat).setOnCheckedChangeListener { _, checkedId ->
            val compact = when (checkedId) {
                R.id.ds_rb_vehicleList_compact -> true
                R.id.ds_rb_vehicleList_detailed -> false
                else -> false
            }
            ConfigService().savePreferencesData(this, Constants.SETTINGS_VEHICLES_LIST_COMPACT, compact)
        }
    }

    /**
     * Configures the format of the providers list in the settings dialog.
     *
     * @param dialogView The view of the settings dialog.
     */
    private fun configureProvidersListFormat(dialogView: View) {
        val compactFormat = ConfigService().getPreferencesBoolean(this, Constants.SETTINGS_PROVIDERS_GRID_FORMAT)
        when (compactFormat) {
            true -> dialogView.findViewById<RadioGroup>(R.id.ds_rg_providersGridListFormat)
                .check(R.id.ds_rb_providers_list_grid)

            false -> dialogView.findViewById<RadioGroup>(R.id.ds_rg_providersGridListFormat)
                .check(R.id.ds_rb_providers_list_linear)
        }
        dialogView.findViewById<RadioGroup>(R.id.ds_rg_providersGridListFormat)
            .setOnCheckedChangeListener { _, checkedId ->
                val compact = when (checkedId) {
                    R.id.ds_rb_providers_list_grid -> true
                    R.id.ds_rb_providers_list_linear -> false
                    else -> false
                }
                ConfigService().savePreferencesData(this, Constants.SETTINGS_PROVIDERS_GRID_FORMAT, compact)
            }
    }

    /**
     * Configures the size of the chart displayed in the settings dialog.
     *
     * @param dialogView The view of the settings dialog.
     */
    private fun configureProvidersChartSize(dialogView: View) {
        val chartSize =
            ConfigService().getPreferencesString(this, Constants.SETTINGS_PROVIDERS_CHART_SIZE).ifEmpty { "3.0" }
        dialogView.findViewById<Slider>(R.id.ds_sl_providers_chart_size).value = chartSize.toFloat()
        dialogView.findViewById<Slider>(R.id.ds_sl_providers_chart_size).addOnChangeListener { _, size, _ ->
            ConfigService().savePreferencesData(
                applicationContext, Constants.SETTINGS_PROVIDERS_CHART_SIZE, size.toString()
            )
        }
    }

    /**
     * Configures the close button in the settings dialog.
     *
     * @param dialogView The view of the settings dialog.
     */
    private fun configureCloseButton(dialogView: View) {
        dialogView.findViewById<MaterialButton>(R.id.ds_bt_close).setOnClickListener {
            alertDialog?.dismiss()
            startActivity(Intent(this, ItemListActivity::class.java))
            finish()
        }
    }

    /**
     * Opens the selected fragment based on the intent data.
     */
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
                val vehicleId = intent.getStringExtra("vehicleId")
                val spentListFragment = SpentsListFragment()
                val bundle = Bundle()
                bundle.putString("vehicleId", vehicleId)
                spentListFragment.arguments = bundle
                navigateToFragment(spentListFragment)
            }
        }
    }

    /**
     * Navigates to the specified fragment by replacing the current fragment in the container without adding it to the back stack.
     *
     * @param fragment The fragment to navigate to.
     */
    override fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.itemList_fragment_container, fragment).commit()

    }

    /**
     * Navigates to the specified fragment by replacing the current fragment in the container and adding it to the back stack.
     *
     * @param fragment The fragment to navigate to.
     */
    private fun navigateToFragmentWithBack(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.itemList_fragment_container, fragment)
            .addToBackStack(null).commit()
    }

    /**
     * Configures the top toolbar of the activity.
     * Observes changes in the toolbar title LiveData and sets the title accordingly.
     * Handles menu item clicks and performs corresponding actions.
     */
    private fun configureTopToolbar() {
        viewModel.toolbarTitle.observe(this) { title -> supportActionBar?.title = title }
        binding.tbTopToolbar.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.tb_it_settings -> {
                    viewModel.setSettingsDialog()
                    true
                }

                R.id.tb_it_aboutMe -> {
                    navigateToFragmentWithBack(AboutMeFragment())
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

    /**
     * Initializes the options menu of the activity's top app bar.
     * Inflates the menu layout resource, sets the user's email as the title of the user email menu item, if available.
     *
     * @param menu The options menu.
     * @return Returns true if the menu is successfully inflated; otherwise, returns false.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_top_app_bar, menu)
        val userEmail = fbGetUserLogged()?.email ?: ""
        val userEmailMenuItem = menu.findItem(R.id.tb_it_userEmail)
        userEmailMenuItem.title = userEmail
        return true
    }

    /**
     * Configures the view model by initializing the item list environment, setting the toolbar title,
     * and setting up the bottom navigation bar.
     */
    private fun configureViewModel() {
        viewModel.initItemListEnvironment(this, binding)
        viewModel.setToolbar(getString(R.string.menu_vehicles), this)
        viewModel.setNavigationBottombar(findViewById(R.id.bottomBar), this)
    }

    /**
     * Observes the sign-out LiveData in the view model.
     * If the sign-out event is triggered, navigates to the LoginActivity.
     */
    private fun signOutAccepted() {
        viewModel.signOut.observe(this) { isSignedOut ->
            if (isSignedOut) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}