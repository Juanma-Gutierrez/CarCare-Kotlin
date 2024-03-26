package com.juanmaGutierrez.carcare.ui.login.loginFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.juanmaGutierrez.carcare.databinding.FragmentLoginBinding
import com.juanmaGutierrez.carcare.service.showSnackBar

interface OnRegisterButtonClickListener {
    fun onRegisterButtonClicked()
}


class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var registerListener: OnRegisterButtonClickListener

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
        binding.loBtRegister.setOnClickListener { registerListener.onRegisterButtonClicked() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showSnackbarEvent.observe(viewLifecycleOwner) { message ->
            showSnackBar(message, view)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRegisterButtonClickListener) {
            registerListener = context
        } else {
            throw RuntimeException("$context must implement OnRegisterButtonClickListener")
        }
    }
}