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

/**
 * ViewModel responsible for user registration logic.
 */
class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var activity: AppCompatActivity
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage
    private val _navigateToItemList = MutableLiveData<Boolean>()
    val navigateToItemList: LiveData<Boolean> get() = _navigateToItemList

    /**
     * Initializes the ViewModel with the activity context.
     */
    fun init(activity: AppCompatActivity) {
        this.activity = activity
    }

    /**
     * Attempts to register a user with the provided data.
     */
    fun register(user: User) {
        if (!validUserData(user)) return
        else try {
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

    /**
     * Validates user data before registration.
     */
    private fun validUserData(user: User): Boolean {
        if (!allFieldsFilled(user)) return false
        if (!emailIsValid(user)) return false
        if (!passwordIsValid(user)) return false
        return true
    }

    /**
     * Validates the email format.
     */
    private fun emailIsValid(user: User): Boolean {
        val regex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"
        if (!user.email.matches(regex.toRegex())) {
            _snackbarMessage.value = getApplication<Application>().getString(R.string.error_emailInvalid)
            return false
        }
        return true
    }

    /**
     * Validates the password format and matching.
     */
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

    /**
     * Validates if the password meets the required characters.
     */
    private fun validatePasswordCharacters(password: String): Boolean {
        val pattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
        return pattern.matches(password)
    }

    /**
     * Validates if all registration fields are filled.
     */
    private fun allFieldsFilled(user: User): Boolean {
        val invalid =
            user.name.isEmpty() or user.surname.isEmpty() or user.username.isEmpty() or user.email.isEmpty() or user.password.isEmpty()
        if (invalid) {
            _snackbarMessage.value = getApplication<Application>().getString(R.string.error_emptyFields)
            return false
        }
        return true
    }
}