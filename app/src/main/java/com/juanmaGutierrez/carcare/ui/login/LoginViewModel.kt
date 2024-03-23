package com.juanmaGutierrez.carcare.ui.login

import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    fun login() {
        System.out.println("boton LOGIN")
    }

    fun register() {
        System.out.println("boton REGISTRO")
    }
}