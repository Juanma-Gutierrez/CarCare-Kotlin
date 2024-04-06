package com.juanmaGutierrez.carcare.ui.detailActivity.viewModel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juanmaGutierrez.carcare.databinding.ActivityDetailBinding


class DetailViewModel : ViewModel() {
    lateinit var activity: AppCompatActivity
    private lateinit var binding: ActivityDetailBinding
    private val _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String> get() = _toolbarTitle
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage

    fun init(activity: AppCompatActivity, fragmentType: String) {
        this.activity = activity
    }

    fun setToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }

}