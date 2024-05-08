package com.juanmaGutierrez.carcare.ui.login.loginFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.service.fbSaveUserLocally
import com.juanmaGutierrez.carcare.service.saveToLog
import com.juanmaGutierrez.carcare.ui.login.LoginActivity


class LoginViewModel : ViewModel() {
    // private lateinit var auth: FirebaseAuth
    private lateinit var activity: LoginActivity
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _navigateToItemList = MutableLiveData<Boolean>()
    val navigateToItemList: LiveData<Boolean> get() = _navigateToItemList

    fun init(activity: LoginActivity) {
        this.activity = activity
    }

    private fun navigateItemList() {
        _navigateToItemList.value = true
    }

    fun login(fragment: LoginFragment, email: String, password: String) {
        if (!validInputs(email, password)) {
            _snackbarMessage.value = fragment.getString(R.string.snackBar_fieldsEmpty)
            return
        }
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(fragment.requireActivity()) { task ->
            if (task.isSuccessful) {
                saveToLog(LogType.INFO, OperationLog.LOGIN, Constants.LOGIN_SUCCESFULLY) { fbSaveUserLocally(auth) }
                navigateItemList()
            } else {
                _snackbarMessage.value = fragment.getString(R.string.snackBar_inputError)
                Log.e(Constants.TAG_ERROR, Constants.LOGIN_FAILURE_SING_IN_WITH_EMAIL, task.exception)
            }
        }
    }

    fun checkUserIsLogged() {
        if (userIsLogged()) {
            Log.i(Constants.TAG, Constants.LOGIN_USER_LOGGED)
            navigateItemList()
        }
    }

    private fun userIsLogged(): Boolean {
        val auth = Firebase.auth
        if (auth.currentUser != null) {
            fbSaveUserLocally(auth)
            Log.i(Constants.TAG, Constants.LOGIN_SUCCESFULLY)
        } else {
            Log.e(Constants.TAG_ERROR, Constants.LOGIN_ERROR)
        }
        return (auth.currentUser != null)
    }

    private fun validInputs(email: String, password: String): Boolean {
        return ((email != "") and (password != ""))
    }
}