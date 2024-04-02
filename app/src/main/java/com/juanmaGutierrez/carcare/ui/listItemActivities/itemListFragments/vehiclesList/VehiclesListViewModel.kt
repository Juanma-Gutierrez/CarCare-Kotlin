package com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments.vehiclesList

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.localData.AppDatabase
import com.juanmaGutierrez.carcare.localData.UserLocalData
import com.juanmaGutierrez.carcare.localData.VehicleEntity
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.ui.mainActivity.MainActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VehiclesListViewModel (
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    lateinit var activity: AppCompatActivity
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var binding: ActivityItemListBinding
    private lateinit var vehicleBinding: FragmentVehiclesListBinding
    private val vehicleDao = MainActivity.database.vehicleDao()
    val _vehicleList = MutableLiveData<List<VehicleEntity>>()
    val vehicleList: LiveData<List<VehicleEntity>>
        get() = _vehicleList

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

    fun loadVehiclesFromRoom(activity: AppCompatActivity = this.activity): List<VehicleEntity> {
        val appDatabase = AppDatabase.getInstance(activity.applicationContext)
        val vehicleDao = appDatabase.vehicleDao()
        var vehiclesFiltered: List<VehicleEntity> = emptyList()
        GlobalScope.launch(dispatcher) {
            val vehicles = vehicleDao.getVehicles()
            vehiclesFiltered = filtercheckAvailablesVehicles(vehicles, vehicleBinding.veSwSwitchAllVehicles.isChecked)
            vehicleAdapter.updateData(vehiclesFiltered)
        }
        return vehiclesFiltered
    }

    fun filtercheckAvailablesVehicles(vehicles: List<VehicleEntity>, switch: Boolean): List<VehicleEntity> {
        if (switch) return vehicles
        return vehicles.filter { it.available }
    }

    suspend fun saveFBVehiclesToRoom() {
        val fb = FirebaseService.getInstance()
        while (fb.userID.isEmpty()) {
            delay(100)
        }
        val db = Firebase.firestore
        Log.d("wanma", "fb userid ${fb.userID}")
        val docRef = db.collection("user").document(fb.userID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    saveLocalUser(document.data)
                    if (document.data != null) {
                        val vehiclesList = document.data!!["vehicles"] as List<Map<String, Any>>
                        _vehicleList.value = mapVehiclesList(vehiclesList)
                        saveVehiclesLocally(_vehicleList.value!!)
                    }
                } else {
                    Log.e("ERROR", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Get failed with ", exception)
            }
    }

    private fun saveLocalUser(data: Map<String, Any>?) {
        val user = UserLocalData.getInstance()
        user.userID = data!!["userId"].toString()
    }

    fun mapVehiclesList(vehicles: List<Map<String, Any>>): List<VehicleEntity> {
        val vehicleEntities = vehicles.map { vehicleData ->
            VehicleEntity(
                vehicleId = vehicleData["vehicleId"].toString(),
                userId = vehicleData["userId"].toString(),
                ref = vehicleData["ref"].toString(),
                created = vehicleData["created"].toString(),
                registrationDate = vehicleData["registrationDate"].toString(),
                available = vehicleData["available"] as Boolean,
                model = vehicleData["model"].toString(),
                plate = vehicleData["plate"].toString(),
                category = vehicleData["category"].toString(),
                brand = vehicleData["brand"].toString()
            )
        }
        return vehicleEntities
    }

    fun saveVehiclesLocally(vehicles: List<VehicleEntity>) {
        viewModelScope.launch {
            vehicleDao.insertVehicles(vehicles)
        }
    }

}