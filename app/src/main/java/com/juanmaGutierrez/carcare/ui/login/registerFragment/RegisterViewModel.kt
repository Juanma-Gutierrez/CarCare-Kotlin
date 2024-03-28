package com.juanmaGutierrez.carcare.ui.login.registerFragment

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentRegisterBinding
import com.juanmaGutierrez.carcare.model.User
import com.juanmaGutierrez.carcare.service.Constants.Companion.TAG
import com.juanmaGutierrez.carcare.service.fbRegisterUserAuth

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var activity: AppCompatActivity
    private val _showSnackbarEvent = MutableLiveData<String>()
    val showSnackbarEvent: LiveData<String>
        get() = _showSnackbarEvent

    fun init(activity: AppCompatActivity) {
        this.activity = activity
    }

    fun register(binding: FragmentRegisterBinding) {
        val user: User = readUser(binding)
        if (!validUserData(user)) return
        else
            try {
                fbRegisterUserAuth(user)
                _showSnackbarEvent.value =
                    activity.getString(R.string.snackBar_registerUser_Successfully)
            } catch (e: Error) {
                Log.e(TAG, "Error in register user")
            }
    }

    private fun validUserData(user: User): Boolean {
        if (someFieldEmpty(user)) {
            _showSnackbarEvent.value =
                getApplication<Application>().getString(R.string.error_emptyFields)
            return false
        }
        if (!passwordIsValid(user)) return false
        return true
    }

    private fun passwordIsValid(user: User): Boolean {
        if (user.password != user.repeatPassword) {
            _showSnackbarEvent.value =
                getApplication<Application>().getString(R.string.error_passwordNotMatch)
            return false
        }
        if (!validatePasswordCharacters(user.password)) {
            _showSnackbarEvent.value =
                getApplication<Application>().getString(R.string.error_passwordWeak)
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

    fun readUser(binding: FragmentRegisterBinding): User {
        return User(
            name = binding.reItName.text.toString(),
            surname = binding.reItSurname.text.toString(),
            username = binding.reItUsername.text.toString(),
            email = binding.reItEmail.text.toString(),
            password = binding.reItPassword.text.toString(),
            repeatPassword = binding.reItRepeatPassword.text.toString()
        )
    }
}