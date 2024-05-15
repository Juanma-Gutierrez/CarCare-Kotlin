package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.juanmaGutierrez.carcare.mapping.mapHashVehiclesToList
import com.juanmaGutierrez.carcare.mapping.mapSpentListFBToSpentList
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.model.localData.UIUserMessages
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.service.fbGetDocumentByID
import kotlinx.coroutines.launch

class SpentsListViewModel : ViewModel() {
    private val _vehicles = MutableLiveData<List<VehiclePreview>>()
    val vehicles: LiveData<List<VehiclePreview>> get() = _vehicles
    private val _selectedVehicle = MutableLiveData<VehiclePreview>()
    val selectedVehicle: LiveData<VehiclePreview> get() = _selectedVehicle
    private val _spentsList = MutableLiveData<List<SpentFB>>()
    val spentsList: LiveData<List<SpentFB>> get() = _spentsList
    private val _numSpents = MutableLiveData<Int>()
    val numSpents: LiveData<Int> get() = _numSpents
    private val _totalSpents = MutableLiveData<Double>()
    val totalSpents: LiveData<Double> get() = _totalSpents
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _editSpentSuccessful = MutableLiveData<Boolean>()
    val editSpentSuccessful: LiveData<Boolean> get() = _editSpentSuccessful
    lateinit var uiUM: UIUserMessages


    fun getVehiclesListFromFB() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val uid = FirebaseService.getInstance().auth?.uid.toString()
            fbGetDocumentByID(uid, Constants.FB_COLLECTION_USER) { v ->
                val data = v?.data as? Map<String, List<Map<String, String>>>
                if (data != null) {
                    val rawVehicles = data["vehicles"] as List<HashMap<String, Any>>
                    val vehiclePreviewList = mapHashVehiclesToList(rawVehicles)
                    _vehicles.value = vehiclePreviewList
                } else {
                    Log.e(Constants.TAG, Constants.ERROR_FIREBASE_CALL)
                }
                _isLoading.postValue(false)
            }
        }
    }

    fun vehicleClicked(vehicle: VehiclePreview) {
        _selectedVehicle.value = vehicle
        viewModelScope.launch {
            fbGetDocumentByID(vehicle.vehicleId, Constants.FB_COLLECTION_VEHICLE) { vehicleSnapshot ->
                convertSpentsFBToSpents(vehicleSnapshot!!)
                calculateNumSpents()
                calculateTotalSpents()
            }
        }
    }

    private fun convertSpentsFBToSpents(vehicleSnapshot: DocumentSnapshot) {
        val rawSpents = vehicleSnapshot.data?.get("spents") as List<Map<String, Any>>
        _spentsList.value = mapSpentListFBToSpentList(rawSpents)
    }

    private fun calculateNumSpents() {
        _numSpents.value = _spentsList.value?.size ?: 0
    }

    private fun calculateTotalSpents() {
        var amount = 0.0
        if (!_spentsList.value.isNullOrEmpty()) {
            for (spent in _spentsList.value!!) {
                amount += spent.amount
            }
        }
        _totalSpents.value = amount
    }
}