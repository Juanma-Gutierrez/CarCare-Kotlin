package com.juanmaGutierrez.carcare.ui.detailActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityDetailBinding
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.detailActivity.fragment.provider.ProviderDetailFragment
import com.juanmaGutierrez.carcare.ui.detailActivity.fragment.spent.SpentDetailFragment
import com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle.VehicleDetailFragment
import com.juanmaGutierrez.carcare.ui.detailActivity.viewModel.DetailViewModel

/**
 * Activity for displaying detail fragments.
 */
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private var itemId: String = ""

    /**
     * Called when the activity is starting. This is where most initialization should be done:
     * calling setContentView(int) to inflate the activity's UI, and creating and initializing fragments.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     * then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Note: Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        itemId = getIdFromItem()
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        val fragmentType = intent.getStringExtra("fragmentType")
        viewModel.init(this, fragmentType ?: "")
        viewModel.toolbarTitle.observe(this) { title ->
            this.setSupportActionBar(this.findViewById(R.id.topAppBarDetail))
            this.supportActionBar?.title = title
            setBackButton()
        }
        viewModel.snackbarMessage.observe(this) { message ->
            showSnackBar(
                message,
                findViewById(android.R.id.content)
            ) {}
        }
        setNavigationAndTitle(fragmentType)
    }

    /**
     * Sets navigation and title according to the fragment type.
     * @param fragmentType The type of the fragment to be displayed.
     */
    private fun setNavigationAndTitle(fragmentType: String?) {
        when (fragmentType) {
            "newVehicle" -> {
                viewModel.setToolbarTitle(getString(R.string.new_vehicle))
                navigateToDetailFragment(VehicleDetailFragment())
            }

            "editVehicle" -> {
                viewModel.setToolbarTitle(getString(R.string.edit_vehicle))
                navigateToDetailFragment(VehicleDetailFragment(), itemId)
            }

            "newProvider" -> {
                viewModel.setToolbarTitle(getString(R.string.new_provider))
                navigateToDetailFragment(ProviderDetailFragment())
            }

            "editProvider" -> {
                viewModel.setToolbarTitle(getString(R.string.edit_provider))
                navigateToDetailFragment(ProviderDetailFragment(), itemId)
            }

            "newSpent" -> {
                viewModel.setToolbarTitle(getString(R.string.new_spent))
                val vehicleId = intent.getStringExtra("vehicleId") ?: ""
                navigateToDetailFragment(SpentDetailFragment(), "", vehicleId)
            }

            "editSpent" -> {
                viewModel.setToolbarTitle(getString(R.string.edit_spent))
                val vehicleId = intent.getStringExtra("vehicleId")
                navigateToDetailFragment(SpentDetailFragment(), itemId, vehicleId)
            }
        }
    }

    /**
     * Retrieves the item ID from the intent extras.
     * @return The item ID.
     */
    private fun getIdFromItem(): String {
        val itemId = intent.getStringExtra("itemId")
        return itemId ?: ""
    }

    /**
     * Sets the click listener for the back button on the toolbar.
     */
    private fun setBackButton() {
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBarDetail)
        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Navigates to the corresponding detail fragment.
     * @param fragment The fragment to navigate to.
     * @param itemId The item ID to pass to the fragment.
     * @param vehicleID The vehicle ID to pass to the fragment.
     */
    private fun navigateToDetailFragment(fragment: Fragment, itemId: String = "", vehicleID: String? = "") {
        fragment.apply {
            val bundle = Bundle()
            bundle.putString("itemId", itemId)
            bundle.putString("vehicleId", vehicleID)
            arguments = bundle
        }
        supportFragmentManager.beginTransaction().replace(R.id.itemDetail_fragment_container, fragment).commit()
    }
}
