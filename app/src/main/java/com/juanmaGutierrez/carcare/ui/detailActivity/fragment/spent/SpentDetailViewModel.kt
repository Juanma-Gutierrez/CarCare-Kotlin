package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.spent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanmaGutierrez.carcare.mapping.mapProvidersListRawToProvidersList
import com.juanmaGutierrez.carcare.mapping.mapSpentFBToSpent
import com.juanmaGutierrez.carcare.mapping.mapSpentListFBToSpentList
import com.juanmaGutierrez.carcare.mapping.mapVehicleFBToVehicle
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.model.localData.Spent
import com.juanmaGutierrez.carcare.model.localData.UIUserMessages
import com.juanmaGutierrez.carcare.service.fbGetAuthUserUID
import com.juanmaGutierrez.carcare.service.fbGetDocumentByID
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
import kotlinx.coroutines.launch

class SpentDetailViewModel : ViewModel() {
    private val _spent = MutableLiveData<Spent>()
    val spent: LiveData<Spent> get() = _spent
    private val _providersSelectableList = MutableLiveData<List<String>>()
    val providersSelectableList: LiveData<List<String>> get() = _providersSelectableList
    private val _selectedVehicle = MutableLiveData<VehicleFB>()
    val selectedVehicle: LiveData<VehicleFB> get() = _selectedVehicle
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _editSpentSuccessful = MutableLiveData<Boolean>()
    val editSpentSuccessful: LiveData<Boolean> get() = _editSpentSuccessful
    lateinit var uiUM: UIUserMessages

    fun init() {
        _spent.value = Spent()
    }

    fun getSpentFromFB(itemId: String, vehicleId: String) {
        viewModelScope.launch {
            fbGetDocumentByID(vehicleId, Constants.FB_COLLECTION_VEHICLE) { vehicle ->
                if (vehicle != null) {
                    _selectedVehicle.value = mapVehicleFBToVehicle(vehicle)
                    val spents = _selectedVehicle.value?.spents as List<HashMap<String, Any>>
                    val filteredSpentFB = mapSpentListFBToSpentList(spents).filter { it.spentId == itemId }
                    _spent.value = mapSpentFBToSpent(filteredSpentFB[0])
                }
            }
        }
    }

    fun getProviders() {
        var providersListRaw: MutableList<Provider>
        setIsLoading(true)
        viewModelScope.launch {
            val uid = fbGetAuthUserUID()
            fbGetDocumentByID(uid, Constants.FB_COLLECTION_PROVIDER) { provider ->
                val providers = provider?.get("providers") as List<HashMap<String, Any>>
                providersListRaw = mapProvidersListRawToProvidersList(providers)
                _providersSelectableList.value =
                    providersListRaw.map { it.name.toUpperCamelCase() }.sorted().toMutableList()
                setIsLoading(false)
            }
        }
    }

    fun setSpent(spent: Spent) {
        _spent.value = spent
    }

    internal fun setIsLoading(status: Boolean) {
        this._isLoading.value = status
    }
}