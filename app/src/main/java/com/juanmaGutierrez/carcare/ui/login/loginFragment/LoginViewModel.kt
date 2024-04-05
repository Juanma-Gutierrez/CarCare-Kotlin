package com.juanmaGutierrez.carcare.ui.login.loginFragment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.LogType
import com.juanmaGutierrez.carcare.model.OperationLog
import com.juanmaGutierrez.carcare.service.Constants
import com.juanmaGutierrez.carcare.service.fbSaveUserLocally
import com.juanmaGutierrez.carcare.service.fbCreateLog
import com.juanmaGutierrez.carcare.service.fbSaveLog
import com.juanmaGutierrez.carcare.ui.login.LoginActivity


class LoginViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private lateinit var activity: LoginActivity
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _navigateToItemList = MutableLiveData<Boolean>()
    val navigateToItemList: LiveData<Boolean> get() = _navigateToItemList

    @RequiresApi(Build.VERSION_CODES.O)
    fun init(activity: LoginActivity) {
        this.activity = activity
    }

    private fun navigateItemList() {
        _navigateToItemList.value = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun login(fragment: LoginFragment, email: String, password: String) {
        if (!validInputs(email, password)) {
            _snackbarMessage.value = fragment.getString(R.string.snackBar_fieldsEmpty)
            return
        }
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(fragment.requireActivity()) { task ->
                if (task.isSuccessful) {
                    saveLoginToLog(auth.currentUser)
                    fbSaveUserLocally(auth.currentUser!!)
                    navigateItemList()
                } else {
                    _snackbarMessage.value = fragment.getString(R.string.snackBar_inputError)
                    Log.e(Constants.TAG_ERROR, Constants.LOGIN_FAILURE_SING_IN_WITH_EMAIL, task.exception)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveLoginToLog(currentUser: FirebaseUser?) {
        fbSaveLog(
            fbCreateLog(
                LogType.INFO,
                auth.currentUser!!,
                currentUser?.uid,
                OperationLog.LOGIN,
                Constants.LOGIN_SUCCESFULLY
            )
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun checkUserIsLogged() {
        if (userIsLogged()) {
            Log.i(Constants.TAG, Constants.LOGIN_USER_LOGGED)
            navigateItemList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun userIsLogged(): Boolean {
        auth = Firebase.auth
        if (auth.currentUser != null) {
            fbSaveUserLocally(auth.currentUser!!)
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