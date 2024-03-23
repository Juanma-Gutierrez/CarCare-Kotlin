package com.juanmaGutierrez.carcare.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityLoginBinding
import com.juanmaGutierrez.carcare.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.loBtLogin.setOnClickListener { login() }
        binding.loBtRegister.setOnClickListener { register() }
        return binding.root
    }

    private fun login() {
        System.out.println("boton LOGIN")
    }

    private fun register() {
        System.out.println("boton REGISTRO")
    }

}