package com.juanmaGutierrez.carcare.ui.detailActivity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityDetailBinding
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle.VehicleDetailFragment
import com.juanmaGutierrez.carcare.ui.detailActivity.viewModel.DetailViewModel
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList.VehiclesListFragment

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

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
        }
        viewModel.snackbarMessage.observe(this) { message -> showSnackBar(message, findViewById(android.R.id.content)) }
        when (fragmentType) {
            "newVehicle" -> {
                viewModel.setToolbarTitle(getString(R.string.new_vehicle))
                navigateToNewVehiclesFragment(VehicleDetailFragment())
            }

            "editVehicle" -> {
                viewModel.setToolbarTitle(getString(R.string.edit_vehicle))
                navigateToNewVehiclesFragment(VehicleDetailFragment()) // TODO Cambiar
            }
        }
    }


    private fun navigateToNewVehiclesFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.itemDetail_fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
