package com.juanmaGutierrez.carcare.ui.login

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityLoginBinding
import com.juanmaGutierrez.carcare.ui.login.loginFragment.LoginFragment
import com.juanmaGutierrez.carcare.ui.login.loginFragment.LoginViewModel
import com.juanmaGutierrez.carcare.ui.login.loginFragment.OnRegisterButtonClickListener
import com.juanmaGutierrez.carcare.ui.login.registerFragment.RegisterFragment

class LoginActivity : AppCompatActivity(), OnRegisterButtonClickListener {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        viewModel.init(this)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.loginFragmentContainer.id, LoginFragment())
        fragmentTransaction.commit()
    }

    override fun onRegisterButtonClicked() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.login_fragment_container, RegisterFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}