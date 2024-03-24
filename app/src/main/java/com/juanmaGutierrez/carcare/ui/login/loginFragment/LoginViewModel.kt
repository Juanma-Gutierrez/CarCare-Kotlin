package com.juanmaGutierrez.carcare.ui.login.loginFragment

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.service.Constants
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.login.LoginActivity
import com.juanmaGutierrez.carcare.ui.vehicles.VehiclesActivity


class LoginViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth

    fun init(activity: LoginActivity) {
        //if (!userIsLogged()) {
        if (userIsLogged()) {
            // TODO Cambiar a -!userIsLogged()- para hacer la comprobación correcta de usuario logueado
            Log.i(Constants.TAG, "User registered")
            // TODO Usar navgraph para ir a vehiculos
            val intent = Intent(activity, VehiclesActivity::class.java)
            activity.startActivity(intent)
        }
    }

    fun login(fragment: LoginFragment, email: String, password: String) {
        if (!validInputs(email, password)) {
            showSnackBar(
                fragment.getString(R.string.snackBar_fieldsEmpty),
                fragment.requireView()
            )
            return
        }
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(fragment.requireActivity()) { task ->
                if (task.isSuccessful) {
                    showSnackBar(
                        fragment.getString(R.string.snackBar_welcome),
                        fragment.requireView()
                    )
                } else {
                    showSnackBar(
                        fragment.getString(R.string.snackBar_inputError),
                        fragment.requireView()
                    )
                    Log.e(Constants.TAG_ERROR, "signInWithEmail:failure", task.exception)
                }
            }
    }

    private fun userIsLogged(): Boolean {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        return (currentUser != null)
    }

    private fun validInputs(email: String, password: String): Boolean {
        return ((email != "") and (password != ""))
    }


}