package com.juanmaGutierrez.carcare.ui.detailActivity.viewModel

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityDetailBinding
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.showSnackBar

class DetailViewModel : ViewModel() {
    lateinit var activity: AppCompatActivity
    private lateinit var binding: ActivityDetailBinding
    private val _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String> get() = _toolbarTitle
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage

    fun init(activity: AppCompatActivity, fragmentType: String) {
        this.activity = activity
        _snackbarMessage.value = fragmentType
    }

    fun setToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }
}