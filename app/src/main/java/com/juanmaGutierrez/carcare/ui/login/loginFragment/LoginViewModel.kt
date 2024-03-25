package com.juanmaGutierrez.carcare.ui.login.loginFragment

import android.content.Intent
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
import com.juanmaGutierrez.carcare.model.ItemLog
import com.juanmaGutierrez.carcare.model.LogType
import com.juanmaGutierrez.carcare.model.OperationLog
import com.juanmaGutierrez.carcare.service.Constants
import com.juanmaGutierrez.carcare.service.fbSaveLog
import com.juanmaGutierrez.carcare.ui.login.LoginActivity
import com.juanmaGutierrez.carcare.ui.vehicles.VehiclesActivity
import java.time.LocalDateTime


class LoginViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private val _showSnackbarEvent = MutableLiveData<String>()
    val showSnackbarEvent: LiveData<String>
        get() = _showSnackbarEvent

    fun init(activity: LoginActivity) {
        if (!userIsLogged()) {
            // TODO Cambiar a -!userIsLogged()- para hacer la comprobación correcta de usuario logueado
            Log.i("wanma", "User registered")
            val intent = Intent(activity, VehiclesActivity::class.java)
            activity.startActivity(intent)
        }
    }

    fun login(fragment: LoginFragment, email: String, password: String) {
        if (!validInputs(email, password)) {
            _showSnackbarEvent.value = fragment.getString(R.string.snackBar_fieldsEmpty)
            return
        }
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(fragment.requireActivity()) { task ->
                if (task.isSuccessful) {
                    _showSnackbarEvent.value = fragment.getString(R.string.snackBar_welcome)
                } else {
                    _showSnackbarEvent.value = fragment.getString(R.string.snackBar_inputError)
                    Log.e(Constants.TAG_ERROR, "signInWithEmail:failure", task.exception)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun userIsLogged(): Boolean {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("wanma", currentUser.email.toString())
            val itemLog = createLog(
                LogType.INFO,
                auth.currentUser!!,
                currentUser.uid,
                OperationLog.LOGIN,
                "Login successfully"
            )
            fbSaveLog(itemLog)
        } else {
            val itemLog = createLog(LogType.ERROR, null, null, OperationLog.LOGIN, "Login failed")
            fbSaveLog(itemLog)
        }
        return (currentUser != null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createLog(
        type: LogType,
        currentUser: FirebaseUser?,
        uid: String? = "",
        operation: OperationLog,
        content: String = "",
    ): ItemLog {
        val email = currentUser?.email ?: ""
        val uid = currentUser?.uid ?: ""
        return ItemLog(LocalDateTime.now(), type, operation, email, uid, content)
    }

    private fun validInputs(email: String, password: String): Boolean {
        return ((email != "") and (password != ""))
    }
}