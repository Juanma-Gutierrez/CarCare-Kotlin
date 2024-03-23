package com.juanmaGutierrez.carcare.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juanmaGutierrez.carcare.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentContainer.id, LoginFragment())
        fragmentTransaction.commit()
    }
}