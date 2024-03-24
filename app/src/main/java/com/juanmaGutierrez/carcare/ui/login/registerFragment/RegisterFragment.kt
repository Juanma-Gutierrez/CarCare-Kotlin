package com.juanmaGutierrez.carcare.ui.login.registerFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.databinding.FragmentRegisterBinding
import com.juanmaGutierrez.carcare.service.showSnackBar

class RegisterFragment : Fragment() {
    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: FragmentRegisterBinding

/*    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        binding.reBtRegister.setOnClickListener { viewModel.register(binding) }
        binding.reBtLogin.setOnClickListener { fragmentManager?.popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]

        viewModel.showSnackbarEvent.observe(viewLifecycleOwner) { message ->
            showSnackBar(message, view)
        }
    }
}