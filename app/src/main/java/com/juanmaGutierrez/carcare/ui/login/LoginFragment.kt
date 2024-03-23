package com.juanmaGutierrez.carcare.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.juanmaGutierrez.carcare.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.loBtLogin.setOnClickListener {
            val email = binding.loItEmail.text.toString()
            val password = binding.loItPassword.text.toString()
            viewModel.login(this, email, password)
        }
        binding.loBtRegister.setOnClickListener { viewModel.register() }
        return binding.root
    }


}