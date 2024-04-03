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
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.service.createLog
import com.juanmaGutierrez.carcare.service.fbSaveLog
import com.juanmaGutierrez.carcare.ui.login.LoginActivity


class LoginViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private lateinit var activity: LoginActivity
    private val _showSnackbarEvent = MutableLiveData<String>()
    val showSnackbarEvent: LiveData<String>
        get() = _showSnackbarEvent
    private val _navigateToItemList = MutableLiveData<Boolean>()
    val navigateToItemList: LiveData<Boolean>
        get() = _navigateToItemList

    @RequiresApi(Build.VERSION_CODES.O)
    fun init(activity: LoginActivity) {
        this.activity = activity
        // checkUserIsLogged()
    }

    private fun navigateItemList() {
        _navigateToItemList.value = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun login(fragment: LoginFragment, email: String, password: String) {
        if (!validInputs(email, password)) {
            _showSnackbarEvent.value = fragment.getString(R.string.snackBar_fieldsEmpty)
            return
        }
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(fragment.requireActivity()) { task ->
                if (task.isSuccessful) {
                    saveLoginToLog(auth.currentUser)
                    val fb = FirebaseService.getInstance()
                    fb.user = auth.currentUser
                    navigateItemList()
                } else {
                    _showSnackbarEvent.value = fragment.getString(R.string.snackBar_inputError)
                    Log.e(Constants.TAG_ERROR, "signInWithEmail:failure", task.exception)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveLoginToLog(currentUser: FirebaseUser?) {
        fbSaveLog(
            createLog(
                LogType.INFO,
                auth.currentUser!!,
                currentUser?.uid,
                OperationLog.LOGIN,
                "Login successfully"
            )
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun checkUserIsLogged() {
        if (userIsLogged()) {
            Log.i(Constants.TAG, "User logged")
            navigateItemList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun userIsLogged(): Boolean {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d(Constants.TAG, "Login successfully")
        } else {
            Log.d(Constants.TAG_ERROR, "Login error")
        }
        return (currentUser != null)
    }


    private fun validInputs(email: String, password: String): Boolean {
        return ((email != "") and (password != ""))
    }
}