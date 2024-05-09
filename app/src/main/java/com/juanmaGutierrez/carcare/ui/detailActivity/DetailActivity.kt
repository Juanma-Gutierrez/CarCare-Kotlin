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
    private var itemID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        itemID = getIDFromItem()
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

    private fun setNavigationAndTitle(fragmentType: String?) {
        when (fragmentType) {
            "newVehicle" -> {
                viewModel.setToolbarTitle(getString(R.string.new_vehicle))
                navigateToDetailFragment(VehicleDetailFragment())
            }

            "editVehicle" -> {
                viewModel.setToolbarTitle(getString(R.string.edit_vehicle))
                navigateToDetailFragment(VehicleDetailFragment(), itemID)
            }

            "newProvider" -> {
                viewModel.setToolbarTitle(getString(R.string.new_provider))
                navigateToDetailFragment(ProviderDetailFragment())
            }

            "editProvider" -> {
                viewModel.setToolbarTitle(getString(R.string.edit_provider))
                navigateToDetailFragment(ProviderDetailFragment(), itemID)
            }

            "newSpent" -> {
                viewModel.setToolbarTitle(getString(R.string.new_spent))
                navigateToDetailFragment(SpentDetailFragment())
            }

            "editSpent" -> {
                viewModel.setToolbarTitle(getString(R.string.edit_spent))
                navigateToDetailFragment(SpentDetailFragment())
            }
        }
    }

    private fun getIDFromItem(): String {
        val itemID = intent.getStringExtra("itemID")
        return itemID ?: ""
    }

    private fun setBackButton() {
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBarDetail)
        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun navigateToDetailFragment(fragment: Fragment, itemID: String = "") {
        fragment.apply {
            val bundle = Bundle()
            bundle.putString("itemID", itemID)
            arguments = bundle
        }
        supportFragmentManager.beginTransaction().replace(R.id.itemDetail_fragment_container, fragment).commit()
    }
}
