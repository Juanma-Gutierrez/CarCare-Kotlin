package com.juanmaGutierrez.carcare.ui.login.registerFragment

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.localData.User
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.Constants.Companion.TAG
import com.juanmaGutierrez.carcare.service.fbRegisterUserAuth

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var activity: AppCompatActivity
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _navigateToItemList = MutableLiveData<Boolean>()
    val navigateToItemList: LiveData<Boolean> get() = _navigateToItemList

    fun init(activity: AppCompatActivity) {
        this.activity = activity
    }

    fun register(user: User) {
        if (!validUserData(user)) return
        else
            try {
                fbRegisterUserAuth(user) { success ->
                    if (success) {
                        FirebaseAuth.getInstance().addAuthStateListener { auth ->
                            if (auth.currentUser != null) {
                                _navigateToItemList.value = true
                            }
                        }
                    }
                }
            } catch (e: Error) {
                Log.e(TAG, Constants.REGISTER_USER_ERROR)
            }
    }

    private fun validUserData(user: User): Boolean {
        if (someFieldEmpty(user)) {
            _snackbarMessage.value = getApplication<Application>().getString(R.string.error_emptyFields)
            return false
        }
        if (!passwordIsValid(user)) return false
        return true
    }

    private fun passwordIsValid(user: User): Boolean {
        if (user.password != user.repeatPassword) {
            _snackbarMessage.value = getApplication<Application>().getString(R.string.error_passwordNotMatch)
            return false
        }
        if (!validatePasswordCharacters(user.password)) {
            _snackbarMessage.value = getApplication<Application>().getString(R.string.error_passwordWeak)
            return false
        }
        return true
    }

    private fun validatePasswordCharacters(password: String): Boolean {
        val pattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
        return pattern.matches(password)
    }

    private fun someFieldEmpty(user: User): Boolean {
        return (user.name.isEmpty() or
                user.surname.isEmpty() or
                user.username.isEmpty() or
                user.email.isEmpty() or
                user.password.isEmpty())
    }
}