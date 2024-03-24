package com.juanmaGutierrez.carcare.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), OnRegisterButtonClickListener {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.loginFragmentContainer.id, LoginFragment())
        fragmentTransaction.commit()
        // viewModel.init(this)
    }

    override fun onRegisterButtonClicked() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.login_fragment_container, RegisterFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}