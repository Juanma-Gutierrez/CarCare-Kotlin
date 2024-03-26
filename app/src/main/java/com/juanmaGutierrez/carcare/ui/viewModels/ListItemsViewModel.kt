package com.juanmaGutierrez.carcare.ui.viewModels

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.ui.providers.ProvidersActivity
import com.juanmaGutierrez.carcare.ui.spents.SpentsActivity
import com.juanmaGutierrez.carcare.ui.vehicles.VehiclesActivity

class ListItemsViewModel : ViewModel() {

    fun setToolbar(title: String, activity: AppCompatActivity) {
        activity.setSupportActionBar(activity.findViewById(R.id.tb_toolbar))
        activity.supportActionBar?.title = title
    }

    fun setBottombar(bottomNavigationView: BottomNavigationView, activity: AppCompatActivity) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_vehicles -> {
                    val intent = Intent(activity, VehiclesActivity::class.java)
                    activity.startActivity(intent)
                    true
                }
                R.id.navigation_providers -> {
                    val intent = Intent(activity, ProvidersActivity::class.java)
                    activity.startActivity(intent)
                    true
                }
                R.id.navigation_spents -> {
                    val intent = Intent(activity, SpentsActivity::class.java)
                    activity.startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}