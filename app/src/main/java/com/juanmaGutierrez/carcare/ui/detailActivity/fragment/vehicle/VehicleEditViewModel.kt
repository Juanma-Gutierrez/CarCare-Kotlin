package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.juanmaGutierrez.carcare.api.APIClient
import com.juanmaGutierrez.carcare.api.APIService
import com.juanmaGutierrez.carcare.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.mapping.mapDocumentDataToVehicle
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.api.BrandsResponseAPI
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.service.fbSetVehicle
import com.juanmaGutierrez.carcare.service.fbSetVehiclePreview
import com.juanmaGutierrez.carcare.service.log
import com.juanmaGutierrez.carcare.service.saveToLog
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class VehicleEditViewModel : ViewModel() {
    private lateinit var apiService: APIService
    var selectedCategory: String = ""
    val vehicle = MutableLiveData<VehicleFB>()
    val editVehicleSuccessful = MutableLiveData<Boolean>()
    private val _modelsList = MutableLiveData<List<String>>()
    val modelsList: LiveData<List<String>> get() = _modelsList
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage

    @RequiresApi(Build.VERSION_CODES.O)
    fun editVehicle(vehicle: VehicleFB) {
        viewModelScope.launch {
            try {
                fbSetVehicle(vehicle).await()
                fbSetVehiclePreview(vehicle).await()
                saveToLog(LogType.INFO, OperationLog.SET_VEHICLE, Constants.LOG_VEHICLE_EDITION_SUCCESSFULLY)
                editVehicleSuccessful.value = true
            } catch (e: Exception) {
                log("Error al editar el vehiculo")
            }
        }
    }

    fun deleteVehicle(vehicle: VehicleFB) {
        // todo borrado de vehículos
    }

    fun init(itemID: String) {
        editVehicleSuccessful.value = false
        val db = Firebase.firestore
        val docRef = db.collection(Constants.FB_COLLECTION_VEHICLE).document(itemID)
        _isLoading.value = true
        docRef.get().addOnSuccessListener { document ->
            if (document.data != null) {
                _isLoading.value = false
                val vehicle = mapDocumentDataToVehicle(document)
                this.vehicle.value = vehicle
            } else {
                Log.e(Constants.TAG_ERROR, Constants.FB_NO_DOCUMENT)
            }
        }.addOnFailureListener { exception ->
            Log.e(Constants.TAG_ERROR, Constants.ERROR_EXCEPTION_PREFIX, exception)
        }
    }

    fun getAllBrandsFromAPI() {
        val retrofit =
            Retrofit.Builder().baseUrl(Constants.API_URL).addConverterFactory(GsonConverterFactory.create()).build()
        apiService = retrofit.create(APIService::class.java)
        viewModelScope.launch {
            callBrandsFromAPI()
        }
    }

    private suspend fun callBrandsFromAPI() {
        _isLoading.value = true
        try {
            val brandsResponse = APIClient.apiService.getAllBrands()
            _isLoading.value = false
            loadBrandsInLocalBrandsService(brandsResponse)
        } catch (e: Exception) {
            Log.e(Constants.TAG_ERROR, "${Constants.ERROR_API_CALL} ${e.message}")
        }
    }

    private fun loadBrandsInLocalBrandsService(brandsResponse: BrandsResponseAPI) {
        val vehiclesBrandSVC = VehicleBrandsService
        vehiclesBrandSVC.carsList = brandsResponse.data.cars.sorted()
        vehiclesBrandSVC.motorcyclesList = brandsResponse.data.motorcycles.sorted()
        vehiclesBrandSVC.vansList = brandsResponse.data.vans.sorted()
        vehiclesBrandSVC.trucksList = brandsResponse.data.trucks.sorted()
    }

    fun loadModelsByBrand(brand: String) {
        viewModelScope.launch {
            getModelsByBrand(brand)
        }
    }

    private suspend fun getModelsByBrand(brand: String) {
        val retrofit =
            Retrofit.Builder().baseUrl(Constants.API_URL).addConverterFactory(GsonConverterFactory.create()).build()
        apiService = retrofit.create(APIService::class.java)
        viewModelScope.launch {
            _modelsList.value = callModelsFromAPI(brand)
        }
    }

    private suspend fun callModelsFromAPI(brand: String): List<String> {
        var data = emptyList<String>()
        _isLoading.value = true
        try {
            data = APIClient.apiService.getModelsByBrand(selectedCategory, brand).models
            _isLoading.value = false
        } catch (e: Exception) {
            Log.e(Constants.TAG_ERROR, "${Constants.ERROR_API_CALL} ${e.message}")
        }
        return data
    }
}
