package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.provider

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentProviderDetailBinding
import com.juanmaGutierrez.carcare.localData.getProviderCategories
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.AlertDialogMessageModel
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.service.getProviderCategoryTranslation
import com.juanmaGutierrez.carcare.service.loadDataInSelectable
import com.juanmaGutierrez.carcare.service.milog
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
import com.juanmaGutierrez.carcare.service.translateProviderCategory
import com.juanmaGutierrez.carcare.ui.itemListActivity.ItemListActivity

class ProviderDetailFragment : Fragment() {
    private lateinit var binding: FragmentProviderDetailBinding
    private lateinit var viewModel: ProviderDetailViewModel
    private lateinit var provider: Provider
    private var itemID = ""
    private var fragmentType = "new"
    private var alertDialogMessage: AlertDialogMessageModel = AlertDialogMessageModel()

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
                binding.pdBtDelete.visibility = View.GONE
                alertDialogMessage.title = getString(R.string.alertDialog_newProvider_title)
                alertDialogMessage.message = getString(R.string.alertDialog_newProvider_message)
                alertDialogMessage.logContentSuccessMessage = Constants.LOG_PROVIDER_CREATION_SUCCESSFULLY
                alertDialogMessage.logContentErrorMessage = Constants.LOG_PROVIDER_CREATION_ERROR
            }

            "edit" -> {
                binding.pdBtDelete.visibility = View.VISIBLE
                alertDialogMessage.title = getString(R.string.alertDialog_editProvider_title)
                alertDialogMessage.message = getString(R.string.alertDialog_editProvider_message)
                alertDialogMessage.logContentSuccessMessage = Constants.LOG_PROVIDER_EDITION_SUCCESSFULLY
                alertDialogMessage.logContentErrorMessage = Constants.LOG_PROVIDER_EDITION_ERROR
            }
        }
        viewModel.alertDialogMessage = alertDialogMessage
    }

    private fun getProviderFromID(): String {
        itemID = arguments?.getString("itemID") ?: ""
        if (itemID != "") {
            viewModel.getProviderFromFB(itemID)
            return "edit"
        }
        return "new"
    }

    private fun configureUI() {
        binding.pdBtAccept.setOnClickListener {
            val provider = getProviderFromForm()
            viewModel.setProviderToFB(provider)

        }
        binding.pdBtCancel.setOnClickListener { closeFragment() }
        binding.pdBtDelete.setOnClickListener { milog("eliminar") }

    }


    private fun getProviderFromForm(): Provider {
        val provider = Provider(
            binding.pdAcCategory.text.toString().translateProviderCategory(),
            this.provider.created,
            binding.pdTvName.text.toString(),
            binding.pdTvPhone.text.toString(),
            this.provider.providerId
        )
        return provider
    }


    private fun configureObservers() {
        configureIsLoadingObserver()
        configureProviderObserver()
        configureEditProviderObserver()
    }

    private fun configureIsLoadingObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
            }
        }
    }

    private fun configureProviderObserver() {
        viewModel.provider.observe(viewLifecycleOwner) { provider ->
            this.provider = provider
            binding.pdTvName.setText(provider.name.toUpperCamelCase())
            binding.pdTvPhone.setText(provider.phone.toUpperCamelCase())
            binding.pdAcCategory.setText(
                provider.category.getProviderCategoryTranslation(requireActivity()), false
            )
        }
    }

    private fun configureEditProviderObserver() {
        viewModel.editProviderSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                showSnackBar(
                    requireActivity().getString(R.string.vehicle_editVehicle_successfully), requireView()
                ) { closeFragmentAndRestart() }
            }
        }
    }

    private fun configureSelectables() {
        val categoriesList = getProviderCategories(requireActivity())
        loadDataInSelectable(binding.pdAcCategory, categoriesList, requireActivity())
    }

    private fun closeFragment() {
        if (isAdded) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun closeFragmentAndRestart() {
        requireActivity().finish()
        val intent = Intent(requireContext(), ItemListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("destinationFragment", "providersList")
        startActivity(intent)
    }
}