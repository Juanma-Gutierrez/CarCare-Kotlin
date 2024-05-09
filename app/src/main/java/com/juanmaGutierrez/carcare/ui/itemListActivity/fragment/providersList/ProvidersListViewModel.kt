package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.providersList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanmaGutierrez.carcare.mapping.mapProviderFBtoProvider
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.Constants.Companion.TAG
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.service.fbGetAuthUserUID
import com.juanmaGutierrez.carcare.service.fbGetDocumentByID
import kotlinx.coroutines.launch

class ProvidersListViewModel : ViewModel() {
    private val _providersList = MutableLiveData<List<Provider>>()
    val providersList: LiveData<List<Provider>> get() = _providersList
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getProvidersListFromFB() {
        _isLoading.value = true
        viewModelScope.launch {
            val userID = fbGetAuthUserUID()
            fbGetDocumentByID(userID, "provider") { providers ->
                val data = providers?.data as? Map<String, List<Map<String, String>>>
                if (data != null) {
                    _providersList.value = mapProviderFBtoProvider(data)
                    _isLoading.value = false
                } else {
                    Log.e(TAG, Constants.ERROR_FIREBASE_CALL)
                }
            }
        }
    }
}