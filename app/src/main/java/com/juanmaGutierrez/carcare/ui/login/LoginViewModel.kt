package com.juanmaGutierrez.carcare.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.juanmaGutierrez.carcare.service.showSnackBar


class LoginViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth


    fun login(fragment: LoginFragment, email: String, password: String) {
        if (!validInputs(email, password)) {
            fragment.view?.let { showSnackBar("Error en los datos", it) }
            return
        }
        /*creo que esto es para confirmar si está ya autenticado
          val currentUser = auth.currentUser
                System.out.println(currentUser)
                if (currentUser != null) {
                    System.out.println(currentUser)
                    // reload()
                }*/
        auth = Firebase.auth
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
                    Log.e("ERROR", "signInWithEmail:failure", task.exception)
                }
            }
    }

    private fun validInputs(email: String, password: String): Boolean {
        return ((email != "") and (password != ""))
    }


    fun register() {
        System.out.println("boton REGISTRO")
    }


}