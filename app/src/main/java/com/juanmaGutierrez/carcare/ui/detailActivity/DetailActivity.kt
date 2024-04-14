package com.juanmaGutierrez.carcare.ui.detailActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityDetailBinding
import com.juanmaGutierrez.carcare.service.log
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle.VehicleDetailFragment
import com.juanmaGutierrez.carcare.ui.detailActivity.viewModel.DetailViewModel
import com.juanmaGutierrez.carcare.ui.itemListActivity.ItemListActivity
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList.VehiclesListFragment
import com.juanmaGutierrez.carcare.ui.login.LoginActivity

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
                navigateToNewVehiclesFragment(VehicleDetailFragment())
            }

            "editVehicle" -> {
                activeFragment = "editVehicle"
                viewModel.setToolbarTitle(getString(R.string.edit_vehicle))
                navigateToNewVehiclesFragment(VehicleDetailFragment()) // TODO Cambiar
            }
        }

    }

    private fun setBackButton() {
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBarDetail)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, ItemListActivity::class.java)
            when (activeFragment) {
                "newVehicle", "editVehicle" -> {
                    intent.putExtra("destinationFragment", "vehiclesList")
                }
                "newProvider", "editProvider" -> {
                    intent.putExtra("destinationFragment", "providersList")
                }
                "newSpent", "editSpent" -> {
                    intent.putExtra("destinationFragment", "spentsList")
                }
            }
            startActivity(intent)
        }
    }


    private fun navigateToNewVehiclesFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.itemDetail_fragment_container, fragment)
        fragmentTransaction.commit()
    }


}
