package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.provider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.juanmaGutierrez.carcare.mapping.mapProviderFBtoProvider
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.ProviderFB
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.model.localData.UIUserMessages
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.service.fbGetDocumentByID
import com.juanmaGutierrez.carcare.service.fbSetDocument
import com.juanmaGutierrez.carcare.service.saveToLog
import kotlinx.coroutines.launch

class ProviderDetailViewModel : ViewModel() {
    private val _provider = MutableLiveData<Provider>()
    val provider: LiveData<Provider> get() = _provider
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _editProviderSuccessful = MutableLiveData<Boolean>()
    val editProviderSuccessful: LiveData<Boolean> get() = _editProviderSuccessful
    lateinit var providers: ProviderFB
    lateinit var uiUM: UIUserMessages

    fun init() {
        providers = ProviderFB(mutableListOf())
    }

    fun getProviderFromFB(itemId: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val uid = FirebaseService.getInstance().auth?.uid.toString()
            fbGetDocumentByID(uid, "provider") { p ->
                val data = p?.data as? Map<String, List<Map<String, String>>>
                if (data != null) {
                    val providersList = mapProviderFBtoProvider(data)
                    providers = ProviderFB(providersList)
                    val providerToAssign = providersList.find { it.providerId == itemId }
                    providerToAssign?.let { _provider.postValue(it) }
                } else {
                    Log.e(Constants.TAG, Constants.ERROR_FIREBASE_CALL)
                }
                _isLoading.postValue(false)
            }
        }
    }

    fun setProviderToFB(provider: Provider) {
        setIsLoading(true)
        updateLocalProvidersList(provider)
        viewModelScope.launch {
            try {
                val auth = FirebaseAuth.getInstance()
                auth.uid?.let { uid -> fbSetDocument(Constants.FB_COLLECTION_PROVIDER, uid, providers) }
                saveToLog(LogType.INFO, OperationLog.PROVIDER, uiUM.logMessages.createOrEditionSuccess)
                _editProviderSuccessful.value = true
            } catch (e: Exception) {
                saveToLog(LogType.ERROR, OperationLog.PROVIDER, uiUM.logMessages.createOrEditionError)
            }
            setIsLoading(false)
        }
    }

    private fun updateLocalProvidersList(provider: Provider) {
        val existingProviderIndex = providers.providers.indexOfFirst { it.providerId == provider.providerId }
        if (existingProviderIndex != -1) {
            providers.providers[existingProviderIndex] = provider
        } else {
            Log.e(Constants.TAG_ERROR, Constants.ERROR_DOCUMENT_DOESNT_EXISTS)
        }
    }

    internal fun setIsLoading(status: Boolean) {
        this._isLoading.value = status
    }

    fun createNewProvider(provider: Provider) {
        setIsLoading(true)
        getProvidersList { providersList ->
            providersList.add(provider)
            providers = ProviderFB(mutableListOf())
            providers.providers = providersList
            saveProvidersListToFB(
                uiUM.logMessages.createOrEditionSuccess, uiUM.logMessages.createOrEditionError
            )
        }
    }

    private fun saveProvidersListToFB(success: String, error: String) {
        viewModelScope.launch {
            try {
                val auth = FirebaseAuth.getInstance()
                auth.uid?.let { uid -> fbSetDocument(Constants.FB_COLLECTION_PROVIDER, uid, providers) }
                saveToLog(LogType.INFO, OperationLog.PROVIDER, success)
                _editProviderSuccessful.value = true
            } catch (e: Exception) {
                saveToLog(LogType.ERROR, OperationLog.PROVIDER, error)
            }
            setIsLoading(false)
        }
    }

    private fun getProvidersList(callback: (MutableList<Provider>) -> Unit) {
        var providersList = mutableListOf<Provider>()
        _isLoading.postValue(true)
        viewModelScope.launch {
            val uid = FirebaseService.getInstance().auth?.uid.toString()
            fbGetDocumentByID(uid, "provider") { p ->
                val data = p?.data as? Map<String, List<Map<String, String>>>
                if (data != null) providersList = mapProviderFBtoProvider(data)
                callback(providersList)
            }
        }
    }

    fun deleteProvider(provider: Provider) {
        setIsLoading(true)
        getProvidersList { providersList ->
            val updatedList = providersList.filter { it.providerId != provider.providerId } as MutableList<Provider>
            providers.providers = updatedList
            saveProvidersListToFB(uiUM.logMessages.deleteSuccess, uiUM.logMessages.deleteError)
        }
    }
}