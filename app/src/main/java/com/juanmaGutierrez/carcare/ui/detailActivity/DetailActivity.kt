package com.juanmaGutierrez.carcare.ui.detailActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.databinding.ActivityDetailBinding
import com.juanmaGutierrez.carcare.ui.detailActivity.viewModel.DetailViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        viewModel.init(this)
        viewModel.initTitle()
        viewModel.toolbarDetailTitle.observe(this) { title -> supportActionBar?.title = title }
    }
}