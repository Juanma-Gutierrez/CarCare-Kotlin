package com.juanmaGutierrez.carcare.ui.mainActivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.juanmaGutierrez.carcare.databinding.ActivityMainBinding
import com.juanmaGutierrez.carcare.localData.DAO.AppDatabase
import com.juanmaGutierrez.carcare.model.Constants

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding


    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.init(this)
        binding.amBtLogin.setOnClickListener { viewModel.init(this) }
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
        viewModel.getAllBrandsFromAPI()
    }
}