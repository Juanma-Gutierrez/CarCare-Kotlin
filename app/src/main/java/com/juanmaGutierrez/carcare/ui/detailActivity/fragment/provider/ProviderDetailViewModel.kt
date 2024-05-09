package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.provider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanmaGutierrez.carcare.mapping.mapProviderFBtoProvider
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.service.fbGetDocumentByID
import kotlinx.coroutines.launch

class ProviderDetailViewModel : ViewModel() {
    private val _provider = MutableLiveData<Provider>()
    val provider: LiveData<Provider> get() = _provider
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage

    fun getProviderFromFB(itemID: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val uid = FirebaseService.getInstance().auth?.uid.toString()
            fbGetDocumentByID(uid, "provider") { providers ->
                val data = providers?.data as? Map<String, List<Map<String, String>>>
                if (data != null) {
                    val providersList = mapProviderFBtoProvider(data)
                    val providerToAssign = providersList.find { it.providerId == itemID }
                    providerToAssign?.let { _provider.postValue(it) }
                } else {
                    Log.e(Constants.TAG, Constants.ERROR_FIREBASE_CALL)
                }
                _isLoading.postValue(false)
            }
        }
    }
}