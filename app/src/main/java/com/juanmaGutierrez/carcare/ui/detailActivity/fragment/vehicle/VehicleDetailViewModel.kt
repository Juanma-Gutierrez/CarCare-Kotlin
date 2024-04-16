package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanmaGutierrez.carcare.api.APIClient
import com.juanmaGutierrez.carcare.api.APIService
import com.juanmaGutierrez.carcare.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.api.BrandsResponseAPI
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VehicleDetailViewModel : ViewModel() {
    private lateinit var apiService: APIService
    private val _modelsList = MutableLiveData<List<String>>()
    val modelsList: LiveData<List<String>> get() = _modelsList
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    var selectedCategory: String = ""

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