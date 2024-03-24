package com.juanmaGutierrez.carcare.ui.login.registerFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.juanmaGutierrez.carcare.databinding.FragmentRegisterBinding
import com.juanmaGutierrez.carcare.model.User
import com.juanmaGutierrez.carcare.service.showSnackBar

class RegisterFragment : Fragment() {
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding.reBtRegister.setOnClickListener { register() }
        binding.reBtLogin.setOnClickListener { goLogin() }
        return binding.root
    }

    private fun register() {
        val user: User = readUser()
        if (!validUserData(user)) return
    }

    private fun validUserData(user: User): Boolean {
        print(user)
        if (someFieldEmpty(user)) {
            showSnackBar("No puedes dejar campos vacíos", requireView())
            return false
        }
        if (!passwordIsValid(user)) return false
        print("usuario válido")
        return true
    }

    private fun passwordIsValid(user: User): Boolean {
        if (!user.password.equals(user.repeatPassword)) {
            showSnackBar("Las contraseñas no coinciden", requireView())
            return false
        }
        if (!validatePasswordCharacters(user.password)) {
            showSnackBar("La contraseña es demasiado débil", requireView())
            return false
        }
        return true
    }

    private fun validatePasswordCharacters(password: String): Boolean {
        val pattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
        return pattern.matches(password)
    }


    private fun someFieldEmpty(user: User): Boolean {
        return (user.name.isEmpty() or
                user.surname.isEmpty() or
                user.username.isEmpty() or
                user.email.isEmpty() or
                user.password.isEmpty())
    }

    private fun readUser(): User {
        return User(
            name = binding.reItName.text.toString(),
            surname = binding.reItSurname.text.toString(),
            username = binding.reItUsername.text.toString(),
            email = binding.reItEmail.text.toString(),
            password = binding.reItPassword.text.toString(),
            repeatPassword = binding.reItRepeatPassword.text.toString()
        )
    }

    private fun goLogin() {
        print("ir a login")
        fragmentManager?.popBackStack()
    }
}