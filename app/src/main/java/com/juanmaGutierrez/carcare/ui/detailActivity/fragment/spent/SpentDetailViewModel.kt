package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.spent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanmaGutierrez.carcare.mapping.mapHashMapSpentToSpent
import com.juanmaGutierrez.carcare.mapping.mapProvidersListRawToProvidersList
import com.juanmaGutierrez.carcare.mapping.mapSpentFBToSpent
import com.juanmaGutierrez.carcare.mapping.mapSpentListFBToSpentList
import com.juanmaGutierrez.carcare.mapping.mapVehicleFBToVehicle
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.model.localData.ProviderSelectable
import com.juanmaGutierrez.carcare.model.localData.Spent
import com.juanmaGutierrez.carcare.model.localData.UIUserMessages
import com.juanmaGutierrez.carcare.service.fbGetAuthUserUID
import com.juanmaGutierrez.carcare.service.fbGetDocumentByID
import com.juanmaGutierrez.carcare.service.fbSetDocument
import com.juanmaGutierrez.carcare.service.saveToLog
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * ViewModel responsible for managing data related to the SpentDetailFragment.
 */
class SpentDetailViewModel : ViewModel() {
    private val _spent = MutableLiveData<Spent>()
    val spent: LiveData<Spent> get() = _spent
    private val _providersSelectableList = MutableLiveData<List<ProviderSelectable>>()
    val providersSelectableList: LiveData<List<ProviderSelectable>> get() = _providersSelectableList
    private val _selectedVehicle = MutableLiveData<VehicleFB>()
    val selectedVehicle: LiveData<VehicleFB> get() = _selectedVehicle
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _editSpentSuccessful = MutableLiveData<Boolean>()
    val editSpentSuccessful: LiveData<Boolean> get() = _editSpentSuccessful
    lateinit var uiUM: UIUserMessages

    /**
     * Initializes the ViewModel with default values.
     */
    fun init() {
        _spent.value = Spent()
    }

    /**
     * Retrieves a spent item from Firebase based on the provided item and vehicle IDs.
     */
    fun getSpentFromFB(itemId: String, vehicleId: String) {
        viewModelScope.launch {
            fbGetDocumentByID(vehicleId, Constants.FB_COLLECTION_VEHICLE) { vehicle ->
                _selectedVehicle.value = mapVehicleFBToVehicle(vehicle!!)
                if (itemId.isNotBlank()) {
                    val spents = _selectedVehicle.value?.spents as List<HashMap<String, Any>>
                    val filteredSpentFB = mapSpentListFBToSpentList(spents).filter { it.spentId == itemId }
                    if (filteredSpentFB.isNotEmpty())
                        _spent.value = mapSpentFBToSpent(filteredSpentFB[0])
                }
            }
        }
    }

    /**
     * Retrieves a list of providers from Firebase.
     */
    fun getProviders() {
        var providersListRaw: MutableList<Provider>
        setIsLoading(true)
        viewModelScope.launch {
            val uid = fbGetAuthUserUID()
            fbGetDocumentByID(uid, Constants.FB_COLLECTION_PROVIDER) { provider ->
                val providers = provider?.get("providers") as List<HashMap<String, Any>>
                providersListRaw = mapProvidersListRawToProvidersList(providers)
                _providersSelectableList.value =
                    providersListRaw.map { provider ->
                        ProviderSelectable(
                            name = provider.name.toUpperCamelCase(),
                            providerId = provider.providerId
                        )
                    }.sortedBy { it.name }.toMutableList()
                setIsLoading(false)
            }
        }
    }

    /**
     * Sets the current spent item.
     */
    fun setSpent(spent: Spent) {
        _spent.value = spent
    }

    /**
     * Sets the loading status.
     */
    internal fun setIsLoading(status: Boolean) {
        this._isLoading.value = status
    }

    /**
     * Saves a vehicle to Firebase.
     */
    fun saveVehicleToFB(vehicle: VehicleFB, successMessage: String, errorMessage: String) {
        try {
            fbSetDocument(Constants.FB_COLLECTION_VEHICLE, vehicle.vehicleId, vehicle)
            saveToLog(LogType.INFO, OperationLog.SPENT, successMessage)
            _editSpentSuccessful.value = true
        } catch (e: Exception) {
            saveToLog(LogType.ERROR, OperationLog.SPENT, errorMessage)
        }
    }

    /**
     * Deletes a spent item.
     */
    fun deleteSpent(itemId: String, vehicleId: String) {
        setIsLoading(true)
        getSpentFromFB(itemId, vehicleId)
        val spentsList = selectedVehicle.value!!.spents as List<HashMap<String, Any>>
        val formattedSpentsList = spentsList.map { mapHashMapSpentToSpent(it) }
        val updatedList = formattedSpentsList.filter { it.spentId != itemId }
        val rawVehicle = selectedVehicle.value!!
        val vehicle = VehicleFB(
            rawVehicle.available,
            rawVehicle.brand,
            rawVehicle.category,
            rawVehicle.created,
            rawVehicle.imageURL,
            rawVehicle.model,
            rawVehicle.plate,
            rawVehicle.registrationDate,
            updatedList,
            rawVehicle.userId,
            rawVehicle.vehicleId,
        )
        saveVehicleToFB(vehicle, uiUM.logMessages.deleteSuccess, uiUM.logMessages.deleteError)
    }

    /**
     * Converts a number string to locale-specific format.
     */
    fun convertNumberToLocale(number: String): Double {
        try {
            val location = Locale.UK
            val formatter = NumberFormat.getInstance(location)
            return formatter.parse(number.replace(",", "."))?.toDouble() ?: 0.0
        } catch (e: Exception) {
            return 0.0
        }
    }
}