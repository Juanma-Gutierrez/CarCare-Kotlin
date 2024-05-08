package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.providersList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanmaGutierrez.carcare.mapping.mapProviderFBtoProvider
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.service.fbGetAuthUserUID
import com.juanmaGutierrez.carcare.service.fbGetDocumentByIDFB
import com.juanmaGutierrez.carcare.service.milog
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
            milog(userID)
            fbGetDocumentByIDFB(userID, "provider") { providers ->
                milog(providers?.data.toString())
                _providersList.value = mapProviderFBtoProvider(providers?.data)
                _providersList.value = providers?.data.map { provider -> mapProviderFBtoProvider(provider) }
                _isLoading.value = false
            }

        }
    }

    private fun mapProviderFBtoProvider(p: Map.Entry<String, Any>): Provider {
// TODO rellenar el provider antes de devolverlo
        // val provider = Provider()
    }
}