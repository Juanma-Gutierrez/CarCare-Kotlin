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
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.mainActivity.MainActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VehiclesListViewModel(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    lateinit var activity: AppCompatActivity
    private val _vehicleList = MutableLiveData<List<VehicleEntity>>()
    val vehiclesList: LiveData<List<VehicleEntity>> get() = _vehicleList
    private val vehicleDao = MainActivity.database.vehicleDao()

    /*    private lateinit var vehicleAdapter: VehicleAdapter
        private lateinit var binding: ActivityItemListBinding
        private lateinit var vehicleBinding: FragmentVehiclesListBinding
        */

    init {
        _vehicleList.value = emptyList()
    }

    fun loadLocalVehicles(context: Context) {
        viewModelScope.launch {
            val localVehicles = withContext(Dispatchers.IO) {
                getLocalVehicles(context)
            }
            _vehicleList.value = localVehicles
        }
    }

    suspend fun getLocalVehicles(context: Context): List<VehicleEntity> {
        return withContext(Dispatchers.IO) {
            val appDatabase = AppDatabase.getInstance(context.applicationContext)
            val vehicleDao = appDatabase.vehicleDao()
            val vehicles = vehicleDao.getVehicles()
            if (vehicles.isNotEmpty()) {
                vehicles
            } else {
                // todo añadir mensaje de que se cree algún vehículo
                emptyList()
            }
        }
    }


    suspend fun saveFBVehiclesToRoom() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val fb = FirebaseService.getInstance()
                /*        while (fb.user.not) {
                            delay(100)
                        }*/
                val db = Firebase.firestore
                val docRef = db.collection("user").document(fb.user!!.uid)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document.data != null) {
                            val vehiclesList = document.data!!["vehicles"] as List<Map<String, Any>>
                            _vehicleList.value = mapVehiclesList(vehiclesList)
                            saveVehiclesLocally(_vehicleList.value!!)
                            //}
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


    /*        val appDatabase = AppDatabase.getInstance(activity.applicationContext)
            val vehicleDao = appDatabase.vehicleDao()
            var vehiclesFiltered: List<VehicleEntity> = emptyList()
            GlobalScope.launch(dispatcher) {
                val vehicles = vehicleDao.getVehicles()
                *//*            vehiclesFiltered = filtercheckAvailablesVehicles(vehicles, vehicleBinding.veSwSwitchAllVehicles.isChecked)
                        vehicleAdapter.updateData(vehiclesFiltered)*//*
            Log.d("wanma", "Tamaño de la lista de vehículos cargado: ${vehicles.size}")
        }
        return vehiclesFiltered*/


    fun addVehiclesWithDelay() {
        CoroutineScope(Dispatchers.IO).launch {
            CoroutineScope(Dispatchers.IO).launch {
                // Simular un retraso de 5 segundos
                delay(5000)

                // Obtener el valor actual de _vehicleList
                val currentList = _vehicleList.value.orEmpty().toMutableList()

                // Agregar vehículos a la lista
                currentList.add(
                    VehicleEntity(
                        vehicleId = "1",
                        userId = "user1",
                        ref = "ref1",
                        created = "created1",
                        registrationDate = "registrationDate1",
                        available = true,
                        model = "model1",
                        plate = "plate1",
                        category = "category1",
                        brand = "brand1"
                    )
                )
                currentList.add(
                    VehicleEntity(
                        vehicleId = "2",
                        userId = "user2",
                        ref = "ref2",
                        created = "created2",
                        registrationDate = "registrationDate2",
                        available = true,
                        model = "model2",
                        plate = "plate2",
                        category = "category2",
                        brand = "brand2"
                    )
                )
                currentList.add(
                    VehicleEntity(
                        vehicleId = "3",
                        userId = "user3",
                        ref = "ref3",
                        created = "created3",
                        registrationDate = "registrationDate3",
                        available = true,
                        model = "model3",
                        plate = "plate3",
                        category = "category3",
                        brand = "brand3"
                    )
                )

                // Establecer el nuevo valor en _vehicleList
                _vehicleList.postValue(currentList)
            }
        }
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






 */
