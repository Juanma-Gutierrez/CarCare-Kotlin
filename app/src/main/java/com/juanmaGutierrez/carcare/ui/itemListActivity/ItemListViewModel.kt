package com.juanmaGutierrez.carcare.ui.itemListActivity

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.Constants.Companion.TAG
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.service.saveToLog
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.providersList.ProvidersListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList.SpentsListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList.VehiclesListFragment
import com.juanmaGutierrez.carcare.ui.mainActivity.MainActivity
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the item list environment.
 */
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

    /**
     * Interface for navigation callbacks.
     */
    fun interface NavigationListener {
        fun navigateToFragment(fragment: Fragment)
    }

    /**
     * Sets the navigation listener.
     *
     * @param listener The navigation listener to set.
     */
    fun setNavigationListener(listener: NavigationListener) {
        navigationListener = listener
    }

    /**
     * Initializes the item list environment.
     *
     * @param activity The AppCompatActivity instance.
     * @param binding The binding object for the activity.
     */
    fun initItemListEnvironment(
        activity: AppCompatActivity,
        binding: ActivityItemListBinding,
    ) {
        this.activity = activity
        this.binding = binding
        this._signOut.value = false
    }

    /**
     * Sets the toolbar title.
     *
     * @param title The title to set.
     * @param activity The AppCompatActivity instance.
     */
    fun setToolbar(title: String, activity: AppCompatActivity) {
        activity.setSupportActionBar(activity.findViewById(R.id.topAppBar))
        activity.supportActionBar?.title = title
    }

    /**
     * Sets up the navigation bottom bar.
     *
     * @param bottomNavigationView The BottomNavigationView instance.
     * @param activity The AppCompatActivity instance.
     */
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

    /**
     * Replaces the current fragment with the specified one.
     *
     * @param fragment The fragment to replace with.
     * @param title The title of the fragment.
     * @return True if the replacement was successful, false otherwise.
     */
    private fun replaceFragment(
        fragment: Fragment, title: String
    ): Boolean {
        setToolbarTitle(title)
        navigationListener?.navigateToFragment(fragment)
        return true
    }

    /**
     * Sets the toolbar title.
     *
     * @param title The title to set.
     */
    private fun setToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }

    /**
     * Shows the sign out dialog.
     */
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

    /**
     * Signs the user out.
     */
    private fun signOut() {
        saveToLog(LogType.INFO, OperationLog.LOGOUT, Constants.LOGOUT_SUCCESSFULLY) {
            clearRoomDatabase {
                try {
                    FirebaseService.getInstance().auth!!.signOut()
                    FirebaseAuth.getInstance().signOut()
                    Log.i(TAG, "Firebase auth logout successfully")
                    FirebaseFirestore.getInstance().terminate().addOnCompleteListener {
                        Firebase.firestore.clearPersistence().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.i(TAG, "Firebase cache deleted successfully")
                            } else {
                                Log.e(TAG, "Error clearing cache: ${task.exception}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error closing session: ${e.message}", e)
                }
            }
        }
    }

    /**
     * Clears the Room database.
     *
     * @param callback The callback to be executed after clearing the database.
     */
    private fun clearRoomDatabase(callback: () -> Unit) {
        val vehicleDao = MainActivity.database.vehicleDao()
        viewModelScope.launch {
            vehicleDao.clearVehicles()
            callback()
        }
    }

    /**
     * Shows the settings dialog.
     */
    fun setSettingsDialog() {
        _openSettingsDialog.value = true
    }
}