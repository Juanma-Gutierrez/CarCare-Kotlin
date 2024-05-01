package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.localData.DAO.AppDatabase
import com.juanmaGutierrez.carcare.localData.entity.VehicleEntity
import com.juanmaGutierrez.carcare.mapping.mapVehiclesListEntityToVehiclesList
import com.juanmaGutierrez.carcare.mapping.mapVehiclesListRawToVehicleEntityList
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.ui.mainActivity.MainActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VehiclesListViewModel(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    private val _vehicleList = MutableLiveData<List<VehiclePreview>>()
    val vehiclesList: LiveData<List<VehiclePreview>> get() = _vehicleList
    private val vehicleDao = MainActivity.database.vehicleDao()
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        _vehicleList.value = emptyList()
    }

    fun loadLocalVehicles(context: Context) {
        viewModelScope.launch {
            val appDatabase = AppDatabase.getInstance(context.applicationContext)
            val vehicleDao = appDatabase.vehicleDao()
            val vehicles = vehicleDao.getVehicles()
            if (vehicles.isNotEmpty()) {
                _vehicleList.value = mapVehiclesListEntityToVehiclesList(vehicles)
            } else {
                _snackbarMessage.value = context.getString(R.string.vehiclesList_noVehicles)
                _vehicleList.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun getFBVehiclesAndSaveFBVehiclesToRoom() {
        viewModelScope.launch {
            withContext(dispatcher) {
                val fb = FirebaseService.getInstance()
                val db = Firebase.firestore
                val docRef = db.collection(Constants.FB_COLLECTION_USER).document(fb.user!!.uid)
                docRef.get().addOnSuccessListener { document ->
                    if (document.data != null) {
                        val vehiclesListRaw = document.data!![Constants.FB_EXTRA_VEHICLES] as List<Map<String, Any>>
                        val vehiclesListEntity = mapVehiclesListRawToVehicleEntityList(vehiclesListRaw)
                        saveVehiclesToRoom(vehiclesListEntity)
                        _vehicleList.value = mapVehiclesListEntityToVehiclesList(vehiclesListEntity)
                    } else {
                        Log.e(Constants.TAG_ERROR, Constants.FB_NO_DOCUMENT)
                    }
                }.addOnFailureListener { exception ->
                    Log.e(Constants.TAG_ERROR, Constants.ERROR_EXCEPTION_PREFIX, exception)
                }
            }
        }
    }

    fun saveVehiclesToRoom(vehicles: List<VehicleEntity>) {
        viewModelScope.launch {
            vehicleDao.replaceAllVehicles(vehicles)
        }
    }

    fun filtercheckAvailablesVehicles(vehicles: List<VehiclePreview>, switch: Boolean): List<VehiclePreview> {
        if (switch) return vehicles
        return vehicles.filter { it.available }
    }
}

