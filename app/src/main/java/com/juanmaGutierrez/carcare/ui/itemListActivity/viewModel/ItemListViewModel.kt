package com.juanmaGutierrez.carcare.ui.itemListActivity.viewModel

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.Constants.Companion.TAG
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.service.saveToLog
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.providersList.ProvidersListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList.SpentsListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList.VehiclesListFragment
import com.juanmaGutierrez.carcare.ui.mainActivity.MainActivity
import kotlinx.coroutines.launch

class ItemListViewModel : ViewModel() {
    private lateinit var binding: ActivityItemListBinding
    lateinit var activity: AppCompatActivity
    private val _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String> get() = _toolbarTitle
    private val _openSettingsDialog = MutableLiveData<Boolean>()
    val openSettingsDialog: LiveData<Boolean> get() = _openSettingsDialog
    private val _signOut = MutableLiveData<Boolean>()
    val signOut: LiveData<Boolean> get() = _signOut
    private var navigationListener: NavigationListener? = null

    fun interface NavigationListener {
        fun navigateToFragment(fragment: Fragment)
    }

    fun setNavigationListener(listener: NavigationListener) {
        navigationListener = listener
    }

    fun initItemListEnvironment(
        activity: AppCompatActivity,
        binding: ActivityItemListBinding,
    ) {
        this.activity = activity
        this.binding = binding
        this._signOut.value = false
    }

    fun setToolbar(title: String, activity: AppCompatActivity) {
        activity.setSupportActionBar(activity.findViewById(R.id.topAppBar))
        activity.supportActionBar?.title = title
    }

    fun setNavigationBottombar(
        bottomNavigationView: BottomNavigationView, activity: AppCompatActivity
    ) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_vehicles -> replaceFragment(
                    VehiclesListFragment(), activity.getString(R.string.menu_vehicles)
                )

                R.id.navigation_providers -> replaceFragment(
                    ProvidersListFragment(), activity.getString(R.string.menu_providers)
                )

                R.id.navigation_spents -> replaceFragment(
                    SpentsListFragment(), activity.getString(R.string.menu_spents)
                )

                else -> false
            }
        }
    }

    private fun replaceFragment(
        fragment: Fragment, title: String
    ): Boolean {
        setToolbarTitle(title)
        navigationListener?.navigateToFragment(fragment)
        return true
    }

    private fun setToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }

    fun setSignOutDialog() {
        val ad = AlertDialogModel(
            activity,
            activity.getString(R.string.logout_title),
            activity.getString(R.string.alertDialog_logout_message),
            AppCompatResources.getDrawable(activity, R.drawable.icon_sign_out)
        )
        showDialogAcceptCancel(ad) { accept ->
            if (accept) {
                signOut()
                this._signOut.value = true
            } else {
                showSnackBar(
                    activity.getString(R.string.cancel_message), activity.findViewById(android.R.id.content)
                ) {}
            }
        }
    }

    private fun signOut() {
        saveToLog(LogType.INFO, OperationLog.LOGOUT, Constants.LOGOUT_SUCCESSFULLY) {
            clearRoomDatabase {
                try {
                    FirebaseAuth.getInstance().signOut()
                    Log.i(TAG, "Firebase auth logout successfully")
                    Firebase.firestore.clearPersistence().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i(TAG, "Firebase cache deleted successfully")
                        } else {
                            Log.e(TAG, "Error clearing cache: ${task.exception}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error closing session: ${e.message}", e)
                }
            }
        }
    }

    private fun clearRoomDatabase(callback: () -> Unit) {
        val vehicleDao = MainActivity.database.vehicleDao()
        viewModelScope.launch {
            vehicleDao.clearVehicles()
            callback()
        }
    }


    fun setSettingsDialog() {
        _openSettingsDialog.value = true
    }
}