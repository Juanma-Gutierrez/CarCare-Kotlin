package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.provider

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentProviderDetailBinding
import com.juanmaGutierrez.carcare.localData.getProviderCategories
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.model.localData.UIUserMessages
import com.juanmaGutierrez.carcare.service.generateId
import com.juanmaGutierrez.carcare.service.getProviderCategoryTranslation
import com.juanmaGutierrez.carcare.service.getTimestamp
import com.juanmaGutierrez.carcare.service.loadDataInSelectable
import com.juanmaGutierrez.carcare.service.milog
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
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
    private var uiUM: UIUserMessages = UIUserMessages()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProviderDetailBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ProviderDetailViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createEmptyProvider()
        configureUIMessages()
        checkNewOrCreate()
        configureUI()
        configureSelectables()
        configureObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createEmptyProvider() {
        this.provider = Provider(
            "gasStation", getTimestamp(), "", "", generateId()
        )
    }

    private fun configureUIMessages() {
        uiUM.snackbarMessages.deleteSuccessful = getString(R.string.provider_deleteProvider_successfully)
        uiUM.snackbarMessages.deletionError = getString(R.string.provider_deleteProvider_error)
    }

    private fun checkNewOrCreate() {
        fragmentType = getProviderFromID()
        when (fragmentType) {
            "new" -> configureNewProviderUI()
            "edit" -> configureEditProviderUI()
        }
        viewModel.alertDialogMessage = uiUM
    }

    private fun configureNewProviderUI() {
        viewModel.setIsLoading(false)
        binding.pdBtDelete.visibility = View.GONE
        uiUM.alertDialog.title = getString(R.string.alertDialog_newProvider_title)
        uiUM.alertDialog.message = getString(R.string.alertDialog_newProvider_message)
        uiUM.snackbarMessages.createOrEditSuccessful = getString(R.string.provider_createProvider_successfully)
        uiUM.snackbarMessages.createOrEditError = getString(R.string.provider_createProvider_error)
        uiUM.logMessages.success = Constants.LOG_PROVIDER_CREATION_SUCCESSFULLY
        uiUM.logMessages.error = Constants.LOG_PROVIDER_CREATION_ERROR
    }

    private fun configureEditProviderUI() {
        binding.pdBtDelete.visibility = View.VISIBLE
        uiUM.alertDialog.title = getString(R.string.alertDialog_editProvider_title)
        uiUM.alertDialog.message = getString(R.string.alertDialog_editProvider_message)
        uiUM.snackbarMessages.createOrEditSuccessful = getString(R.string.provider_editProvider_successfully)
        uiUM.snackbarMessages.createOrEditError = getString(R.string.provider_editProvider_error)
        uiUM.logMessages.success = Constants.LOG_PROVIDER_EDITION_SUCCESSFULLY
        uiUM.logMessages.error = Constants.LOG_PROVIDER_EDITION_ERROR
    }

    private fun configureUI() {
        binding.pdBtAccept.setOnClickListener { buttonAcceptPressed() }
        binding.pdBtCancel.setOnClickListener { buttonCancelPressed() }
        binding.pdBtDelete.setOnClickListener { buttonDeletePressed() }
    }

    private fun configureSelectables() {
        val categoriesList = getProviderCategories(requireActivity())
        loadDataInSelectable(binding.pdAcCategory, categoriesList, requireActivity())
    }

    private fun configureObservers() {
        configureIsLoadingObserver()
        configureProviderObserver()
        configureEditProviderObserver()
    }

    private fun getProviderFromID(): String {
        itemID = arguments?.getString("itemID") ?: ""
        if (itemID != "") {
            viewModel.getProviderFromFB(itemID)
            return "edit"
        }
        return "new"
    }

    private fun buttonAcceptPressed() {
        provider = getProviderFromForm()
        val valid = checkValidForm(provider)
        if (valid) {
            val title = uiUM.alertDialog.title
            val message = uiUM.alertDialog.message
            val icon = AppCompatResources.getDrawable(requireActivity(), R.drawable.icon_edit)
            val ad = AlertDialogModel(this.requireActivity(), title, message, icon)
            showDialogAcceptCancel(ad) { accept ->
                if (accept) {
                    try {
                        acceptButtonClicked()
                    } catch (e: Exception) {
                        Log.e(Constants.TAG, Constants.ERROR_DATABASE, e)
                    }
                }
            }
        } else {
            showSnackBar("completa los datos", requireView()) {}
        }
    }

    private fun acceptButtonClicked() {
        if (fragmentType == "new") {
            viewModel.createNewProvider(provider)
        } else {
            viewModel.setProviderToFB(provider)
        }
    }

    private fun checkValidForm(provider: Provider): Boolean {
        if (provider.name == "") return false
        if (provider.category == "") return false
        return true
    }

    private fun buttonCancelPressed() {
        if (isAdded) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun buttonDeletePressed() {
        milog("eliminar")
    }

    private fun getProviderFromForm(): Provider {
        val provider = Provider(
            binding.pdAcCategory.text.toString().translateProviderCategory(),
            this.provider.created,
            binding.pdTvName.text.toString(),
            binding.pdTvPhone.text.toString(),
            this.provider.providerId
        )
        milog(provider.toString())
        return provider
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
                    requireActivity().getString(R.string.provider_editProvider_successfully), requireView()
                ) { closeFragmentAndRestart() }
            }
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