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

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    var activeFragment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        val fragmentType = intent.getStringExtra("fragmentType")
        viewModel.init(this, fragmentType ?: "")
        viewModel.toolbarTitle.observe(this) { title ->
            this.setSupportActionBar(this.findViewById(R.id.topAppBarDetail))
            this.supportActionBar?.title = title
            setBackButton()
        }
        viewModel.snackbarMessage.observe(this) { message -> showSnackBar(message, findViewById(android.R.id.content)) }
        when (fragmentType) {
            "newVehicle" -> {
                activeFragment = "newVehicle"
                viewModel.setToolbarTitle(getString(R.string.new_vehicle))
                navigateToDetailFragment(VehicleDetailFragment())
            }

            "editVehicle" -> {
                activeFragment = "editVehicle"
                viewModel.setToolbarTitle(getString(R.string.edit_vehicle))
                navigateToDetailFragment(VehicleDetailFragment()) // TODO Pasar el id del vehículo
            }

            "newProvider" -> {
                activeFragment = "newProvider"
                viewModel.setToolbarTitle(getString(R.string.new_provider))
                navigateToDetailFragment(ProviderDetailFragment())
            }

            "editProvider" -> {
                activeFragment = "editProvider"
                viewModel.setToolbarTitle(getString(R.string.edit_provider))
                navigateToDetailFragment(ProviderDetailFragment())
            }

            "newSpent" -> {
                activeFragment = "newSpent"
                viewModel.setToolbarTitle(getString(R.string.new_spent))
                navigateToDetailFragment(SpentDetailFragment())
            }

            "editSpent" -> {
                activeFragment = "editSpent"
                viewModel.setToolbarTitle(getString(R.string.edit_spent))
                navigateToDetailFragment(SpentDetailFragment())
            }
        }

    }

    private fun setBackButton() {
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBarDetail)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    private fun navigateToDetailFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.itemDetail_fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
