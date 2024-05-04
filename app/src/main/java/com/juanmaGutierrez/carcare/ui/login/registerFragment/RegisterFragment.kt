package com.juanmaGutierrez.carcare.ui.login.registerFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.databinding.FragmentRegisterBinding
import com.juanmaGutierrez.carcare.model.localData.User
import com.juanmaGutierrez.carcare.service.milog
import com.juanmaGutierrez.carcare.service.showSnackBar

class RegisterFragment : Fragment() {
    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        configureButtons()
        return binding.root
    }

    private fun configureButtons() {
        binding.reBtRegister.setOnClickListener {
            val user = getUserFromForm()
            viewModel.register(user)
        }
        binding.reBtLogin.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    private fun getUserFromForm(): User {
        val user = User(
            name = binding.reItName.text.toString(),
            surname = binding.reItSurname.text.toString(),
            username = binding.reItUsername.text.toString(),
            email = binding.reItEmail.text.toString(),
            password = binding.reItPassword.text.toString(),
            repeatPassword = binding.reItRepeatPassword.text.toString()
        )
        return user
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]
        viewModel.init(activity as AppCompatActivity)
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            showSnackBar(message, view) {}
        }
        viewModel.navigateToItemList.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                requireActivity().onBackPressed()
            }
        }
    }
}