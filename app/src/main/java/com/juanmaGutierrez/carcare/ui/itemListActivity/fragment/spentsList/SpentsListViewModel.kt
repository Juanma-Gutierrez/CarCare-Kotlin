package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.juanmaGutierrez.carcare.mapping.mapHashVehiclesToListVehiclePreview
import com.juanmaGutierrez.carcare.mapping.mapSpentListFBToSpentList
import com.juanmaGutierrez.carcare.mapping.mapVehicleFBToVehicle
import com.juanmaGutierrez.carcare.mapping.mapVehicleToVehiclePreview
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.model.localData.SpentByProviderForChart
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.service.fbGetDocumentByID
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the spents list.
 */
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
    private val _numSpentsHeightLayout = MutableLiveData<Int>()
    val numSpentsHeightLayout: LiveData<Int> get() = _numSpentsHeightLayout
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _providerCount = MutableLiveData<Int>()
    val providerCount: LiveData<Int> get() = _providerCount

    /**
     * Fetches the list of vehicles from Firebase.
     */
    fun getVehiclesListFromFB() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val uid = FirebaseService.getInstance().auth?.uid.toString()
            fbGetDocumentByID(uid, Constants.FB_COLLECTION_USER) { v ->
                val data = v?.data as? Map<String, List<Map<String, String>>>
                if (data != null) {
                    val rawVehicles = data["vehicles"] as List<HashMap<String, Any>>
                    val vehiclePreviewList = mapHashVehiclesToListVehiclePreview(rawVehicles)
                    _vehicles.value =
                        vehiclePreviewList.sortedWith(compareBy<VehiclePreview> { it.category }.thenBy { it.brand }
                            .thenBy { it.model })
                } else {
                    Log.e(Constants.TAG, Constants.ERROR_FIREBASE_CALL)
                }
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Handles the event when a vehicle is clicked.
     *
     * @param vehicle The clicked vehicle.
     */
    fun vehicleClicked(vehicle: VehiclePreview) {
        viewModelScope.launch {
            fbGetDocumentByID(vehicle.vehicleId, Constants.FB_COLLECTION_VEHICLE) { vehicleSnapshot ->
                if (vehicleSnapshot != null) {
                    saveSelectedVehicle(vehicleSnapshot)
                    convertSpentsFBToSpents(vehicleSnapshot)
                    calculateNumSpents()
                    calculateTotalSpents()
                }
            }
        }
    }

    /**
     * Handles the event when a vehicle is selected by its ID.
     *
     * @param vehicleId The ID of the selected vehicle.
     */
    fun vehicleSelectedById(vehicleId: String) {
        _isLoading.postValue(false)
        viewModelScope.launch {
            fbGetDocumentByID(vehicleId, Constants.FB_COLLECTION_VEHICLE) { vehicleSnapshot ->
                if (vehicleSnapshot != null) {
                    saveSelectedVehicle(vehicleSnapshot)
                    convertSpentsFBToSpents(vehicleSnapshot)
                    calculateNumSpents()
                    calculateTotalSpents()
                }
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Saves the selected vehicle from the given document snapshot.
     *
     * @param vehicleSnapshot The document snapshot containing vehicle data.
     */
    private fun saveSelectedVehicle(vehicleSnapshot: DocumentSnapshot) {
        val vehicleFB = mapVehicleFBToVehicle(vehicleSnapshot)
        val vehiclePreview = mapVehicleToVehiclePreview(vehicleFB)
        _selectedVehicle.value = vehiclePreview
    }

    /**
     * Converts the spents data from Firebase to the local data model.
     *
     * @param vehicleSnapshot The document snapshot containing spents data.
     */
    private fun convertSpentsFBToSpents(vehicleSnapshot: DocumentSnapshot) {
        val rawSpents = vehicleSnapshot.data?.get("spents") as List<Map<String, Any>>
        _spentsList.value = mapSpentListFBToSpentList(rawSpents)
    }

    /**
     * Calculates the number of spents.
     */
    private fun calculateNumSpents() {
        _numSpents.value = _spentsList.value?.size ?: 0
    }

    /**
     * Calculates the total amount of spents.
     */
    private fun calculateTotalSpents() {
        var amount = 0.0
        if (!_spentsList.value.isNullOrEmpty()) {
            for (spent in _spentsList.value!!) {
                amount += spent.amount
            }
        }
        _totalSpents.value = amount
    }

    /**
     * Sets the loading state.
     *
     * @param state The loading state.
     */
    fun setIsLoading(state: Boolean) {
        _isLoading.postValue(state)
    }

    /**
     * Generates the chart data from the list of spents.
     *
     * @param spents The list of spents.
     * @param chartSize The size of the chart.
     * @return A list of pairs representing the chart data.
     */
    fun generateChart(spents: List<SpentFB>, chartSize: Int): List<Pair<String, Float>> {
        val mySetRaw = filterByProvider(spents)
        val mySet = convertToLinkedMap(mySetRaw)
        val height = minOf(mySet.entries.size, chartSize)
        _numSpentsHeightLayout.value = height
        val sortedList = mySet.entries.sortedByDescending { it.value }.take(height)
        return sortedList.map { Pair(it.key, it.value) }.sortedBy { it.second }
    }

    /**
     * Gets the size of the chart.
     *
     * @param context The context.
     * @return The chart size.
     */
    fun getChartSize(context: Context): Int {
        val chartSizeString =
            ConfigService().getPreferencesString(context, Constants.SETTINGS_PROVIDERS_CHART_SIZE).ifEmpty { "3.0" }
        if (chartSizeString == "") {
            ConfigService().savePreferencesData(context, Constants.SETTINGS_PROVIDERS_CHART_SIZE, "3.0")
        }
        return chartSizeString.substring(0, 1).toInt()
    }

    /**
     * Filters the spents by provider.
     *
     * @param spents The list of spents.
     * @return A list of spents grouped by provider for the chart.
     */
    private fun filterByProvider(spents: List<SpentFB>): List<SpentByProviderForChart> {
        return spents.groupBy { it.providerName.substring(0, minOf(it.providerName.length, 15)) }
            .map { (providerName, spents) ->
                SpentByProviderForChart(providerName.toUpperCamelCase(), spents.sumOf { it.amount })
            }
    }

    /**
     * Converts the list of spents to a linked map.
     *
     * @param spentsList The list of spents.
     * @return A linked map of provider names to spent amounts.
     */
    private fun convertToLinkedMap(spentsList: List<SpentByProviderForChart>): LinkedHashMap<String, Float> {
        return spentsList.associate { it.providerName to it.amount.toFloat() } as LinkedHashMap<String, Float>
    }

    /**
     * Fetches the provider count from Firebase.
     */
    fun getProviderCount() {
        viewModelScope.launch {
            val uid = FirebaseService.getInstance().auth?.uid.toString()
            fbGetDocumentByID(uid, Constants.FB_COLLECTION_PROVIDER) { v ->
                val data = v?.data as? Map<String, List<Map<String, String>>>
                if (data != null) {
                    _providerCount.value = data["providers"]?.size
                }
            }
        }
    }
}