package com.juanmaGutierrez.carcare.ui.login.loginFragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.juanmaGutierrez.carcare.databinding.FragmentLoginBinding
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.service.saveToLog
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.itemListActivity.ItemListActivity
import com.juanmaGutierrez.carcare.ui.login.LoginActivity

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.init(this.activity as LoginActivity)
        viewModel.checkUserIsLogged()
        binding.loBtLogin.setOnClickListener {
            val email = binding.loItEmail.text.toString()
            val password = binding.loItPassword.text.toString()
            viewModel.login(this, email, password)
        }
        binding.loBtRegister.setOnClickListener { registerListener.onRegisterButtonClicked() }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message -> showSnackBar(message, view) {} }
        viewModel.navigateToItemList.observe(viewLifecycleOwner) {
            saveToLog(LogType.INFO, OperationLog.LOGIN, Constants.LOGIN_SUCCESFULLY)
            val intent = Intent(requireActivity(), ItemListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
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