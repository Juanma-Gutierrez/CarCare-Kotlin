package com.juanmaGutierrez.carcare.ui.listItemActivities

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.localData.UserLocalData
import com.juanmaGutierrez.carcare.localData.VehicleEntity
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.ui.mainActivity.MainActivity
import kotlinx.coroutines.launch

class ListItemsViewModel : ViewModel() {
    private val vehicleDao = MainActivity.database.vehicleDao()

    fun getVehiclesFromUser() {
        val fb = FirebaseService.getInstance()
        print("EN SUBSCRIBEUSER: $fb")
        val db = Firebase.firestore
        val docRef = db.collection("user").document(fb.userID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    saveLocalUser(document.data)
                    Log.d("wanma", "DocumentSnapshot data: ${document.data}")
                    if (document.data != null) {
                        val vehiclesList: List<Map<String, Any>> =
                            document.data!!.get("vehicles") as List<Map<String, Any>>
                        saveVehiclesLocally(vehiclesList)
                    }
                } else {
                    Log.d("wanma", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("wanma", "get failed with ", exception)
            }
    }

    fun saveVehiclesLocally(vehicles: List<Map<String, Any>>) {
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
        Log.d("wanma", vehicleEntities.size.toString())
        viewModelScope.launch {
            vehicleDao.insertVehicles(vehicleEntities)
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
                R.id.navigation_vehicles -> {
                    val intent = Intent(activity, VehiclesActivity::class.java)
                    activity.startActivity(intent)
                    true
                }

                R.id.navigation_providers -> {
                    val intent = Intent(activity, ProvidersActivity::class.java)
                    activity.startActivity(intent)
                    true
                }

                R.id.navigation_spents -> {
                    val intent = Intent(activity, SpentsActivity::class.java)
                    activity.startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}