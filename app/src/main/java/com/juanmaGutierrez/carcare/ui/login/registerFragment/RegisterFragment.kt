package com.juanmaGutierrez.carcare.ui.login.registerFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.databinding.FragmentRegisterBinding
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.itemListActivity.ItemListActivity

class RegisterFragment : Fragment() {
    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        binding.reBtRegister.setOnClickListener { viewModel.register(binding) }
        binding.reBtLogin.setOnClickListener { parentFragmentManager.popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]
        viewModel.init(activity as AppCompatActivity)
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            showSnackBar(message, view)
        }
        viewModel.navigateToItemList.observe(viewLifecycleOwner) {
            val intent = Intent(requireActivity(), ItemListActivity::class.java)
            startActivity(intent)
        }
    }
}