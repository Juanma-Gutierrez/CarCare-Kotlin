package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanmaGutierrez.carcare.api.APIClient
import com.juanmaGutierrez.carcare.api.APIService
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.Vehicle
import com.juanmaGutierrez.carcare.service.log
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VehicleDetailViewModel : ViewModel() {
    private lateinit var apiService: APIService
    private val _modelsList = MutableLiveData<List<String>>()
    val modelsList: LiveData<List<String>> get() = _modelsList

    fun loadModelsByBrand(brand: String) {
        viewModelScope.launch {
            getModelsByBrand(brand)
        }
    }

    private suspend fun getModelsByBrand(brand: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(APIService::class.java)
        viewModelScope.launch {
            _modelsList.value = callModelsFromAPI(brand)
        }
    }

    private suspend fun callModelsFromAPI(brand: String): List<String> {
        var data = emptyList<String>()
        try {
            data = APIClient.apiService.getCarsBrands().brands
            log("tamaño de data en callModelsFromApi: $data, $brand")
        } catch (e: Exception) {
            Log.e(Constants.TAG_ERROR, "${Constants.ERROR_API_CALL} ${e.message}")
        }
        return data
    }
}