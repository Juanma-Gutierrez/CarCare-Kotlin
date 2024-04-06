package com.juanmaGutierrez.carcare.ui.itemListActivity.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityItemListBinding
import com.juanmaGutierrez.carcare.model.LogType
import com.juanmaGutierrez.carcare.model.OperationLog
import com.juanmaGutierrez.carcare.service.fbCreateLog
import com.juanmaGutierrez.carcare.service.fbSaveLog
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.itemListActivity.itemListFragment.providersList.ProvidersListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.itemListFragment.spentsList.SpentsListFragment
import com.juanmaGutierrez.carcare.ui.itemListActivity.itemListFragment.vehiclesList.VehiclesListFragment

class ItemListViewModel : ViewModel() {
    private lateinit var binding: ActivityItemListBinding
    lateinit var activity: AppCompatActivity

    private val _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String> get() = _toolbarTitle
    private val _signOut = MutableLiveData<Boolean>()
    val signOut: LiveData<Boolean> get() = _signOut

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


    @RequiresApi(Build.VERSION_CODES.O)
    fun setSignOutDialog() {
        MaterialAlertDialogBuilder(activity)
            .setTitle(activity.getString(R.string.logout_title))
            .setMessage(activity.getString(R.string.logout_message))
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
                showSnackBar(activity.getString(R.string.cancel_message), activity.findViewById(android.R.id.content))
            }
            .setPositiveButton(activity.getString(R.string.accept)) { _, _ ->
                signOut()
                this._signOut.value = true
            }
            .show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun signOut() {
        val fb = FirebaseAuth.getInstance()
        val itemLog =
            fbCreateLog(LogType.INFO, fb.currentUser, fb.currentUser!!.uid, OperationLog.LOGOUT, "Logout")
        fbSaveLog(itemLog)
        FirebaseAuth.getInstance().signOut();
    }
}