package com.juanmaGutierrez.carcare.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.juanmaGutierrez.carcare.service.Constants
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.vehicles.VehiclesActivity


class LoginViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth

    fun init(activity: LoginActivity) {
        if (userIsLogged()) {
            Log.i(Constants.TAG, "User registered")
            // TODO Usar navgraph para ir a vehiculos
            val intent = Intent(activity, VehiclesActivity::class.java)
            activity.startActivity(intent)
        }
    }

    fun login(fragment: LoginFragment, email: String, password: String) {

        if (!validInputs(email, password)) {
            fragment.view?.let { showSnackBar("Error en los datos introducidos", it) }
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(fragment.requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    showSnackBar("BIENVENIDO ${user!!.email}", fragment.view!!)
                } else {
                    showSnackBar(
                        "Error en el correo electrónico o en la contraseña",
                        fragment.view!!
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


    fun register() {
        System.out.println("boton REGISTRO")
    }


}