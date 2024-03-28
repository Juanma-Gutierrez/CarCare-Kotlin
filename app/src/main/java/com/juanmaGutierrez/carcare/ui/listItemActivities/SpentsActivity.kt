package com.juanmaGutierrez.carcare.ui.listItemActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R

class SpentsActivity : AppCompatActivity() {
    private lateinit var viewModel: ListItemsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spents)
        viewModel = ViewModelProvider(this)[ListItemsViewModel::class.java]
        viewModel.setToolbar(getString(R.string.menu_spents), this)
        viewModel.setNavigationBottombar(findViewById(R.id.bottom_bar),this)
    }
}