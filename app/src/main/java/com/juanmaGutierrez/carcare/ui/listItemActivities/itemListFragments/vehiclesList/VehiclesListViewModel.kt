package com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments.vehiclesList

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.juanmaGutierrez.carcare.localData.AppDatabase
import com.juanmaGutierrez.carcare.localData.VehicleEntity
import com.juanmaGutierrez.carcare.mapping.mapVehiclesListEntity
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.ui.mainActivity.MainActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VehiclesListViewModel(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    lateinit var activity: AppCompatActivity
    private val _vehicleList = MutableLiveData<List<VehicleEntity>>()
    val vehiclesList: LiveData<List<VehicleEntity>> get() = _vehicleList
    private val vehicleDao = MainActivity.database.vehicleDao()
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage

    init {
        _vehicleList.value = emptyList()
    }

    fun loadLocalVehicles(context: Context) {
        viewModelScope.launch {
            val appDatabase = AppDatabase.getInstance(context.applicationContext)
            val vehicleDao = appDatabase.vehicleDao()
            val vehicles = vehicleDao.getVehicles()
            if (vehicles.isNotEmpty()) {
                _snackbarMessage.value = "carga vehículos locales, ${vehicles.size}"
                _vehicleList.value = vehicles
            } else {
                _snackbarMessage.value = "Debes incluir algún vehículo en tu base de datos"
                _vehicleList.value = emptyList()
            }
        }
    }

    suspend fun saveFBVehiclesToRoom() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val fb = FirebaseService.getInstance()
                val db = Firebase.firestore
                val docRef = db.collection("user").document(fb.user!!.uid)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document.data != null) {
                            val vehiclesList = document.data!!["vehicles"] as List<Map<String, Any>>
                            _vehicleList.value = mapVehiclesListEntity(vehiclesList)
                            _snackbarMessage.value = "ha cargado datos de firebase ${vehiclesList.size}"
                            saveVehiclesToRoom(_vehicleList.value!!)
                        } else {
                            Log.e("ERROR", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ERROR", "Get failed with ", exception)
                    }
            }
        }
    }

    fun saveVehiclesToRoom(vehicles: List<VehicleEntity>) {
        viewModelScope.launch {
            vehicleDao.replaceAllVehicles(vehicles)
        }
    }
}


/*
fun initVehiclesEnvironment(
    activity: AppCompatActivity,
    binding: ActivityItemListBinding,
    vehicleBinding: FragmentVehiclesListBinding
) {
    this.activity = activity
    this.binding = binding
    this.vehicleBinding = vehicleBinding
}

fun initVehiclesFragment() {
    val fragmentManager = activity.supportFragmentManager
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.replace(binding.itemListFragmentContainer.id, VehiclesListFragment())
    fragmentTransaction.commit()
}



fun filtercheckAvailablesVehicles(vehicles: List<VehicleEntity>, switch: Boolean): List<VehicleEntity> {
    if (switch) return vehicles
    return vehicles.filter { it.available }
}







 */
