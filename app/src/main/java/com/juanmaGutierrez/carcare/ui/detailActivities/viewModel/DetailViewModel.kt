package com.juanmaGutierrez.carcare.ui.detailActivities.viewModel

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityDetailBinding
import com.juanmaGutierrez.carcare.service.ToolbarService

class DetailViewModel : ViewModel() {
    private lateinit var binding: ActivityDetailBinding
    private val _toolbarDetailTitle = MutableLiveData<String>()
    val toolbarDetailTitle: LiveData<String>
        get() = _toolbarDetailTitle
    lateinit var activity: AppCompatActivity

    fun init(activity: AppCompatActivity) {
        this.activity = activity
    }

    fun initTitle() {
        val ts = ToolbarService.getInstance()
        activity.setSupportActionBar(activity.findViewById(R.id.topAppBarDetail))
        activity.supportActionBar?.title = ts.detailTitle
    }

}