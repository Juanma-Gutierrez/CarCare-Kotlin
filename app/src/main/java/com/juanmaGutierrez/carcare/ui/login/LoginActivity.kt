package com.juanmaGutierrez.carcare.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.ActivityLoginBinding
import com.juanmaGutierrez.carcare.ui.login.loginFragment.LoginFragment
import com.juanmaGutierrez.carcare.ui.login.loginFragment.LoginViewModel
import com.juanmaGutierrez.carcare.ui.login.loginFragment.OnRegisterButtonClickListener
import com.juanmaGutierrez.carcare.ui.login.registerFragment.RegisterFragment

/**
 * Activity responsible for handling user login.
 */
class LoginActivity : AppCompatActivity(), OnRegisterButtonClickListener {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    /**
     * Initializes the activity, sets up the view, and initializes the ViewModel.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel.init(this)
        supportFragmentManager.beginTransaction().replace(binding.loginFragmentContainer.id, LoginFragment()).commit()
    }

    /**
     * Handles the event when the register button is clicked, navigates to the register fragment.
     */
    override fun onRegisterButtonClicked() {
        supportFragmentManager.beginTransaction().replace(R.id.login_fragment_container, RegisterFragment())
            .addToBackStack(null).commit()
    }
}