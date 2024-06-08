package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanmaGutierrez.carcare.api.APIClient
import com.juanmaGutierrez.carcare.api.APIService
import com.juanmaGutierrez.carcare.mapping.mapVehicleFBToVehicle
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.api.BrandsResponseAPI
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.model.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.service.fbDeleteDocumentByID
import com.juanmaGutierrez.carcare.service.fbDeleteVehiclePreview
import com.juanmaGutierrez.carcare.service.fbGetDocumentByID
import com.juanmaGutierrez.carcare.service.fbGetImageURL
import com.juanmaGutierrez.carcare.service.fbSetDocument
import com.juanmaGutierrez.carcare.service.fbSetVehiclePreview
import com.juanmaGutierrez.carcare.service.saveToLog
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * ViewModel for managing vehicle details.
 */
class VehicleDetailViewModel : ViewModel() {
    private lateinit var apiService: APIService
    var selectedCategory: String = ""
    private val _vehicle = MutableLiveData<VehicleFB>()
    val vehicle: LiveData<VehicleFB> get() = _vehicle
    private val _vehicleImage = MutableLiveData<String>()
    val vehicleImage: LiveData<String> get() = _vehicleImage
    private val _editVehicleSuccessful = MutableLiveData<Boolean>()
    val editVehicleSuccessful: LiveData<Boolean> get() = _editVehicleSuccessful
    private val _categoriesList = MutableLiveData<List<String>>()
    val categoriesList: LiveData<List<String>> get() = _categoriesList
    private val _brandsList = MutableLiveData<List<String>>()
    val brandsList: LiveData<List<String>> get() = _brandsList
    private val _modelsList = MutableLiveData<List<String>>()
    val modelsList: LiveData<List<String>> get() = _modelsList
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage

    /**
     * Retrieves a vehicle from Firebase Firestore.
     * @param itemId The ID of the vehicle.
     */
    fun getVehicleFromFB(itemId: String) {
        setIsLoading(true)
        viewModelScope.launch {
            fbGetDocumentByID(itemId, Constants.FB_COLLECTION_VEHICLE) { document ->
                setIsLoading(false)
                if (document != null) {
                    _vehicle.value = mapVehicleFBToVehicle(document)
                    getVehicleImageURL(_vehicle.value!!)
                }
            }
        }
    }

    /**
     * Sets the available categories.
     * @param categories The list of categories.
     */
    fun setCategories(categories: List<String>) {
        _categoriesList.value = categories
    }

    /**
     * Retrieves vehicle brands from the API based on the selected category.
     * @param category The selected vehicle category.
     */
    fun getBrandsFromAPI(category: String) {
        val retrofit =
            Retrofit.Builder().baseUrl(Constants.API_URL).addConverterFactory(GsonConverterFactory.create()).build()
        apiService = retrofit.create(APIService::class.java)
        viewModelScope.launch {
            callBrandsFromAPI()
            val vehiclesBrandSVC = VehicleBrandsService
            when (category) {
                "car" -> _brandsList.value = vehiclesBrandSVC.carsList
                "motorcycle" -> _brandsList.value = vehiclesBrandSVC.motorcyclesList
                "van" -> _brandsList.value = vehiclesBrandSVC.vansList
                "truck" -> _brandsList.value = vehiclesBrandSVC.trucksList
            }
        }
    }

    /**
     * Calls the API to fetch vehicle brands.
     */
    private suspend fun callBrandsFromAPI() {
        _isLoading.value = true
        try {
            val response = APIClient.apiService.getAllBrands()
            _isLoading.value = false
            setBrandsInLocalBrandsService(response)
        } catch (e: Exception) {
            Log.e(Constants.TAG_ERROR, "${Constants.ERROR_FIREBASE_CALL} ${e.message}")
        }
    }

    /**
     * Sets the fetched vehicle brands in the local brand service.
     * @param brandsResponse The API response containing brands.
     */
    private fun setBrandsInLocalBrandsService(brandsResponse: BrandsResponseAPI) {
        val vehiclesBrandSVC = VehicleBrandsService
        vehiclesBrandSVC.carsList = brandsResponse.data.cars.sorted()
        vehiclesBrandSVC.motorcyclesList = brandsResponse.data.motorcycles.sorted()
        vehiclesBrandSVC.vansList = brandsResponse.data.vans.sorted()
        vehiclesBrandSVC.trucksList = brandsResponse.data.trucks.sorted()
    }

    /**
     * Sets the loading status.
     * @param status The loading status.
     */
    private fun setIsLoading(status: Boolean) {
        this._isLoading.value = status
    }

    /**
     * Retrieves vehicle models from the API based on the selected brand.
     * @param brand The selected vehicle brand.
     */
    fun getModelsFromBrandAPI(brand: String) {
        val retrofit =
            Retrofit.Builder().baseUrl(Constants.API_URL).addConverterFactory(GsonConverterFactory.create()).build()
        apiService = retrofit.create(APIService::class.java)
        viewModelScope.launch {
            _modelsList.value = callModelsFromAPI(brand)
        }
    }

    /**
     * Retrieves the vehicle image URL from Firebase.
     * @param vehicle The vehicle to fetch the image for.
     */
    fun getVehicleImageURL(vehicle: VehicleFB) {
        viewModelScope.launch {
            vehicle.imageURL?.let {
                fbGetImageURL(it) { imageURL ->
                    _vehicleImage.value = imageURL
                }
            }
        }
    }

    /**
     * Calls the API to fetch vehicle models.
     * @param brand The selected vehicle brand.
     * @return The list of models.
     */
    private suspend fun callModelsFromAPI(brand: String): List<String> {
        _isLoading.value = true
        return try {
            val response = APIClient.apiService.getModelsByBrand(selectedCategory, brand)
            _isLoading.value = false
            response.models
        } catch (e: Exception) {
            Log.e(Constants.TAG_ERROR, "${Constants.ERROR_FIREBASE_CALL} ${e.message}")
            emptyList()
        }
    }

    /**
     * Edits the vehicle details in Firebase Firestore.
     * @param vehicle The vehicle data to be edited.
     */
    fun editVehicle(vehicle: VehicleFB) {
        viewModelScope.launch {
            try {
                fbSetDocument(Constants.FB_COLLECTION_VEHICLE, vehicle.vehicleId, vehicle)
                fbSetVehiclePreview(vehicle).await()
                saveToLog(LogType.INFO, OperationLog.VEHICLE, Constants.LOG_VEHICLE_EDITION_SUCCESSFULLY)
                _editVehicleSuccessful.value = true
            } catch (e: Exception) {
                saveToLog(LogType.ERROR, OperationLog.VEHICLE, Constants.LOG_VEHICLE_EDITION_ERROR)
            }
        }
    }

    /**
     * Deletes the vehicle from Firebase Firestore.
     * @param vehicle The vehicle to be deleted.
     */
    fun deleteVehicle(vehicle: VehicleFB) {
        viewModelScope.launch {
            try {
                fbDeleteDocumentByID(Constants.FB_COLLECTION_VEHICLE, vehicle.vehicleId)
                fbDeleteVehiclePreview(vehicle)
                saveToLog(LogType.INFO, OperationLog.VEHICLE, Constants.LOG_VEHICLE_DELETION_SUCCESSFULLY)
                _editVehicleSuccessful.value = true
            } catch (e: Exception) {
                saveToLog(LogType.ERROR, OperationLog.VEHICLE, Constants.LOG_VEHICLE_DELETION_SUCCESSFULLY)
            }
        }
    }
}
