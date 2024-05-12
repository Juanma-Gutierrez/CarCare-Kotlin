package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.spent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.model.localData.UIUserMessages
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview

class SpentDetailViewModel : ViewModel() {
    private val _vehicles = MutableLiveData<List<VehiclePreview>>()
    val vehicles: LiveData<List<VehiclePreview>> get() = _vehicles
    private val _spents = MutableLiveData<List<SpentFB>>()
    val spents: LiveData<List<SpentFB>> get() = _spents
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _editSpentSuccessful = MutableLiveData<Boolean>()
    val editSpentSuccessful: LiveData<Boolean> get() = _editSpentSuccessful
    lateinit var uiUM: UIUserMessages

}