package com.juanmaGutierrez.carcare.ui.listItemActivities.viewModel

import android.util.Log
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.localData.AppDatabase
import com.juanmaGutierrez.carcare.localData.UserLocalData
import com.juanmaGutierrez.carcare.localData.VehicleEntity
import com.juanmaGutierrez.carcare.model.createRandomVehicleList
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments.ProvidersListFragment
import com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments.SpentsListFragment
import com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments.VehiclesListFragment
import com.juanmaGutierrez.carcare.ui.mainActivity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ItemListViewModel : ViewModel() {
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var binding: ActivityItemListBinding
    private lateinit var vehicleBinding: FragmentVehiclesListBinding
    lateinit var activity: AppCompatActivity
    private val vehicleDao = MainActivity.database.vehicleDao()
    private val _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String>
        get() = _toolbarTitle
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

    fun initVehicles() {
        val currentFragment =
            activity.supportFragmentManager.findFragmentById(R.id.itemList_fragment_container)
        Log.d("wanma", "TEST: ${currentFragment is VehiclesListFragment}")

        val randomVehicleList = createRandomVehicleList()
        Log.d("wanma", "VEHICULOS: $randomVehicleList")
        vehicleAdapter = VehicleAdapter(randomVehicleList)
        val recyclerView = vehicleBinding.veRvVehicles
        recyclerView.layoutManager = LinearLayoutManager(activity.applicationContext)
        recyclerView.adapter = vehicleAdapter
        loadVehiclesFromRoom()
        val switchAllVehicles = vehicleBinding.veSwSwitchAllVehicles
        switchAllVehicles.setOnCheckedChangeListener { _, _ -> loadVehiclesFromRoom() }
    }

    fun loadVehiclesFromRoom(activity: AppCompatActivity = this.activity): List<VehicleEntity> {
        val appDatabase = AppDatabase.getInstance(activity.applicationContext)
        val vehicleDao = appDatabase.vehicleDao()
        var vehiclesFiltered: List<VehicleEntity> = emptyList()
        GlobalScope.launch(Dispatchers.Main) {
            val vehicles = vehicleDao.getVehicles()
            vehiclesFiltered = checkAvailablesVehicles(vehicles)
            // vehicleAdapter.updateData(vehiclesFiltered)
        }
        return vehiclesFiltered
    }

    private fun checkAvailablesVehicles(vehicles: List<VehicleEntity>): List<VehicleEntity> {
/*        if (vehicleBinding.veSwSwitchAllVehicles.isChecked) {
            return vehicles
        }*/
        return vehicles.filter { it.available }
    }

    suspend fun saveFBVehiclesToRoom() {
        val fb = FirebaseService.getInstance()
        while (fb.userID.isEmpty()) {
            delay(100) // Esperar 100 milisegundos antes de verificar de nuevo
        }
        val db = Firebase.firestore
        Log.d("wanma", "fb userid ${fb.userID}")
        val docRef = db.collection("user").document(fb.userID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    saveLocalUser(document.data)
                    Log.d("wanma", "DocumentSnapshot data: ${document.data}")
                    if (document.data != null) {
                        val vehiclesList: List<Map<String, Any>> =
                            document.data!!.get("vehicles") as List<Map<String, Any>>
                        _vehicleList.value = mapVehiclesList(vehiclesList)
                        saveVehiclesLocally(_vehicleList.value!!)
                        Log.d("wanma","RESULTADO: ${_vehicleList.value}")
                    }
                } else {
                    Log.e("ERROR", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Get failed with ", exception)
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

    private fun saveLocalUser(data: Map<String, Any>?) {
        val user = UserLocalData.getInstance()
        user.userID = data!!["userId"].toString()
    }

    fun setToolbar(title: String, activity: AppCompatActivity) {
        activity.setSupportActionBar(activity.findViewById(R.id.tb_toolbar))
        activity.supportActionBar?.title = title
    }

    fun setNavigationBottombar(
        bottomNavigationView: BottomNavigationView,
        activity: AppCompatActivity
    ) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_vehicles -> replaceFragment(
                    VehiclesListFragment(),
                    activity,
                    activity.getString(R.string.menu_vehicles)
                )

                R.id.navigation_providers -> replaceFragment(
                    ProvidersListFragment(),
                    activity,
                    activity.getString(R.string.menu_providers)
                )

                R.id.navigation_spents -> replaceFragment(
                    SpentsListFragment(), activity,
                    activity.getString(R.string.menu_spents)
                )

                else -> false
            }
        }
    }

    private fun replaceFragment(
        fragment: Fragment,
        activity: AppCompatActivity,
        title: String
    ): Boolean {
        setToolbarTitle(title)
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.itemList_fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        return true
    }

    fun setToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }
}