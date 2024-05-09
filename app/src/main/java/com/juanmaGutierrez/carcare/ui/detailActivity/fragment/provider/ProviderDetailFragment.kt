package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.provider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentProviderDetailBinding
import com.juanmaGutierrez.carcare.localData.getProviderCategories
import com.juanmaGutierrez.carcare.service.getProviderCategoryTranslation
import com.juanmaGutierrez.carcare.service.loadDataInSelectable
import com.juanmaGutierrez.carcare.service.milog
import com.juanmaGutierrez.carcare.service.toUpperCamelCase

class ProviderDetailFragment : Fragment() {
    private lateinit var binding: FragmentProviderDetailBinding
    private lateinit var viewModel: ProviderDetailViewModel
    private var itemID = ""
    private var fragmentType = "new"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProviderDetailBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ProviderDetailViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNewOrCreate()
        configureUI()
        configureSelectables()
        configureObservers()
    }

    private fun checkNewOrCreate() {
        fragmentType = getProviderFromID()
        when (fragmentType) {
            "new" -> {
                binding.veBtDelete.visibility = View.GONE
            }

            "edit" -> {
                binding.veBtDelete.visibility = View.VISIBLE
            }
        }
    }

    private fun getProviderFromID(): String {
        itemID = arguments?.getString("itemID") ?: ""
        if (itemID != "") {
            viewModel.getProviderFromFB(itemID)
            return "edit"
        }
        return "new"
    }

    private fun configureUI() {}


    private fun configureObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
            }
        }
        viewModel.provider.observe(viewLifecycleOwner) { provider ->
            binding.pdTvName.setText(provider.name.toUpperCamelCase())
            binding.pdTvPhone.setText(provider.phone.toUpperCamelCase())
            binding.pdAcCategory.setText(
                provider.category.getProviderCategoryTranslation(requireActivity()), false
            )
        }
    }

    private fun configureSelectables() {
        val categoriesList = getProviderCategories(requireActivity())
        loadDataInSelectable(binding.pdAcCategory, categoriesList, requireActivity())
    }
}