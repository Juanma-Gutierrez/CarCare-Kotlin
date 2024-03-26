package com.juanmaGutierrez.carcare.ui.spents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.ui.viewModels.ListItemsViewModel

class SpentsActivity : AppCompatActivity() {
    private lateinit var viewModel: ListItemsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spents)
        viewModel = ViewModelProvider(this)[ListItemsViewModel::class.java]
        viewModel.setToolbar(getString(R.string.menu_spents), this)
        viewModel.setBottombar(findViewById(R.id.bottom_bar),this)
    }
}