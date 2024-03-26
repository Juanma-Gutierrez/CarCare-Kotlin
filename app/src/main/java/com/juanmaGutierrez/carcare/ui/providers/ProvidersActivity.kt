package com.juanmaGutierrez.carcare.ui.providers

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.BottombarBinding
import com.juanmaGutierrez.carcare.ui.viewModels.ListItemsViewModel

class ProvidersActivity : AppCompatActivity() {
    private lateinit var viewModel: ListItemsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_providers)
        viewModel = ViewModelProvider(this)[ListItemsViewModel::class.java]
        viewModel.setToolbar(getString(R.string.menu_providers), this)
        viewModel.setBottombar(findViewById(R.id.bottom_bar), this)
    }


}