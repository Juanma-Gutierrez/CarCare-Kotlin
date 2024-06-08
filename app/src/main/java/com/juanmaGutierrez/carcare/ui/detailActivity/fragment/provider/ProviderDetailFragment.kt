package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.provider

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
import com.juanmaGutierrez.carcare.service.translateProviderCategory

/**
 * Fragment for displaying details of a provider.
 */
class ProviderDetailFragment : Fragment() {
    private lateinit var binding: FragmentProviderDetailBinding
    private lateinit var viewModel: ProviderDetailViewModel
    private lateinit var provider: Provider
    private var itemId = ""
    private var fragmentType = "new"
    private var uiUM: UIUserMessages = UIUserMessages()

    /**
     * Called to create the view hierarchy associated with the fragment.
     * This method returns the View that is the root of the fragment's layout.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProviderDetailBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ProviderDetailViewModel::class.java]
        return binding.root
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned,
     * but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createEmptyProvider()
        configureUIMessages()
        checkNewOrCreate()
        configureUI()
        configureSelectables()
        configureObservers()
    }

    /**
     * Creates an empty provider object.
     */
    private fun createEmptyProvider() {
        this.provider = Provider(
            "gasStation", getTimestamp(), "", "", generateId()
        )
    }

    /**
     * Configures the UI messages.
     */
    private fun configureUIMessages() {
        uiUM.snackbarMessages.deleteSuccessful = getString(R.string.provider_deleteProvider_successfully)
        uiUM.snackbarMessages.deletionError = getString(R.string.provider_deleteProvider_error)
        uiUM.logMessages.deleteSuccess = Constants.LOG_PROVIDER_DELETION_SUCCESSFULLY
        uiUM.logMessages.deleteError = Constants.LOG_PROVIDER_DELETION_ERROR
    }

    /**
     * Checks whether the fragment is for creating a new provider or editing an existing one.
     */
    private fun checkNewOrCreate() {
        fragmentType = getProviderFromID()
        when (fragmentType) {
            "new" -> configureNewProviderUI()
            "edit" -> configureEditProviderUI()
        }
        viewModel.uiUM = uiUM
    }

    /**
     * Configures the UI for creating a new provider.
     */
    private fun configureNewProviderUI() {
        viewModel.setIsLoading(false)
        binding.pdBtDelete.visibility = View.GONE
        uiUM.alertDialog.title = getString(R.string.alertDialog_newProvider_title)
        uiUM.alertDialog.message = getString(R.string.alertDialog_newProvider_message)
        uiUM.snackbarMessages.createOrEditSuccessful = getString(R.string.provider_createProvider_successfully)
        uiUM.snackbarMessages.createOrEditError = getString(R.string.provider_createProvider_error)
        uiUM.logMessages.createOrEditionSuccess = Constants.LOG_PROVIDER_CREATION_SUCCESSFULLY
        uiUM.logMessages.createOrEditionError = Constants.LOG_PROVIDER_CREATION_ERROR
    }

    /**
     * Configures the UI for editing an existing provider.
     */
    private fun configureEditProviderUI() {
        binding.pdBtDelete.visibility = View.VISIBLE
        uiUM.alertDialog.title = getString(R.string.alertDialog_editProvider_title)
        uiUM.alertDialog.message = getString(R.string.alertDialog_editProvider_message)
        uiUM.snackbarMessages.createOrEditSuccessful = getString(R.string.provider_editProvider_successfully)
        uiUM.snackbarMessages.createOrEditError = getString(R.string.provider_editProvider_error)
        uiUM.logMessages.createOrEditionSuccess = Constants.LOG_PROVIDER_EDITION_SUCCESSFULLY
        uiUM.logMessages.createOrEditionError = Constants.LOG_PROVIDER_EDITION_ERROR
    }

    /**
     * Configures various UI elements and listeners.
     */
    private fun configureUI() {
        binding.pdBtAccept.setOnClickListener { buttonAcceptPressed() }
        binding.pdBtCancel.setOnClickListener { buttonCancelPressed() }
        binding.pdBtDelete.setOnClickListener { buttonDeletePressed() }
    }

    /**
     * Configures selectable categories.
     */
    private fun configureSelectables() {
        val categoriesList = getProviderCategories(requireActivity())
        loadDataInSelectable(binding.pdAcCategory, categoriesList, requireActivity())
    }

    /**
     * Configures observers for LiveData.
     */
    private fun configureObservers() {
        configureIsLoadingObserver()
        configureProviderObserver()
        configureEditProviderObserver()
    }

    /**
     * Retrieves the provider ID from arguments.
     * @return The type of fragment, either "new" or "edit".
     */
    private fun getProviderFromID(): String {
        itemId = arguments?.getString("itemId") ?: ""
        if (itemId != "") {
            viewModel.getProviderFromFB(itemId)
            return "edit"
        }
        return "new"
    }

    /**
     * Handles the accept button click event.
     */
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
            showSnackBar(getString(R.string.error_emptyFields), requireView()) {}
        }
    }

    /**
     * Performs the necessary actions when the accept button is clicked.
     */
    private fun acceptButtonClicked() {
        if (fragmentType == "new") {
            viewModel.createNewProvider(provider)
        } else {
            viewModel.setProviderToFB(provider)
        }
    }

    /**
     * Checks if the form is valid.
     * @return True if the form is valid, false otherwise.
     */
    private fun checkValidForm(provider: Provider): Boolean {
        if (provider.name == "") return false
        if (provider.category == "") return false
        return true
    }

    /**
     * Handles the cancel button click event.
     */
    private fun buttonCancelPressed() {
        if (isAdded) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Handles the delete button click event.
     */
    private fun buttonDeletePressed() {
        val title = getString(R.string.alertDialog_deleteProvider_title)
        val message = getString(R.string.alertDialog_deleteProvider_message)
        val icon = AppCompatResources.getDrawable(requireActivity(), R.drawable.icon_trash)
        val ad = AlertDialogModel(this.requireActivity(), title, message, icon)
        showDialogAcceptCancel(ad) { accept ->
            if (accept) {
                try {
                    deleteButtonClicked()
                } catch (e: Exception) {
                    Log.e(Constants.TAG, Constants.ERROR_DATABASE, e)
                }
            }
        }
    }

    /**
     * Handles the delete button click event.
     */
    private fun deleteButtonClicked() {
        viewModel.deleteProvider(provider)
    }

    /**
     * Retrieves provider data from the form.
     * @return The provider object created from the form data.
     */
    private fun getProviderFromForm(): Provider {
        return Provider(
            binding.pdAcCategory.text.toString().translateProviderCategory(),
            this.provider.created,
            binding.pdTvName.text.toString(),
            binding.pdTvPhone.text.toString(),
            this.provider.providerId
        )
    }

    /**
     * Configures the observer for loading status changes.
     */
    private fun configureIsLoadingObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
            }
        }
    }

    /**
     * Configures the observer for provider data changes.
     */
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

    /**
     * Configures the observer for provider edit success.
     */
    private fun configureEditProviderObserver() {
        viewModel.editProviderSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                showSnackBar(uiUM.snackbarMessages.createOrEditSuccessful, requireView()) { closeFragmentAndRestart() }
            }
        }
    }

    /**
     * Closes the fragment and restarts.
     */
    private fun closeFragmentAndRestart() {
        if (isAdded) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}