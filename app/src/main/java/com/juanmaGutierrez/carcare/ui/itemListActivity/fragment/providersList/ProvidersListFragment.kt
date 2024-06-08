package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.providersList

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.ProviderAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentProvidersListBinding
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

/**
 * A fragment that displays a list of providers.
 */
class ProvidersListFragment : Fragment() {
    private lateinit var viewModel: ProvidersListViewModel
    private lateinit var binding: FragmentProvidersListBinding
    private lateinit var providersAdapter: ProviderAdapter
    private var providersList: List<Provider> = emptyList()

    /**
     * Called to have the fragment instantiate its user interface view. This is optional,
     * and non-graphical fragments can return null (which is the default implementation).
     *
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        configureUI()
        configureObservers()
        configureAddButton()
        getProvidersList()
        loadProvidersListInRV()
        return binding.root
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally tied to Activity.onResume of the containing Activity's lifecycle.
     * */
    override fun onResume() {
        super.onResume()
        getProvidersList()
    }

    /**
     * Configures the user interface elements.
     */
    private fun configureUI() {
        viewModel = ViewModelProvider(this)[ProvidersListViewModel::class.java]
        binding = FragmentProvidersListBinding.inflate(layoutInflater)
    }

    /**
     * Configures the observers for LiveData.
     */
    private fun configureObservers() {
        configureProvidersListObserver()
        configureIsLoadingObserver()
        configureSnackbarMessageObserver()
    }

    /**
     * Configures the observer for the providers list.
     */
    private fun configureProvidersListObserver() {
        viewModel.providersList.observe(viewLifecycleOwner) { providers ->
            providersList = providers
            if (providersList.isEmpty()) {
                showSnackBar(getString(R.string.providersList_noProviders), requireView()) {}
            }
            loadProvidersListInRV()
        }
    }

    /**
     * Configures the observer for the loading state.
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
     * Configures the observer for the snackbar messages.
     */
    private fun configureSnackbarMessageObserver() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message -> showSnackBar(message, requireView()) {} }
    }

    /**
     * Configures the click listener for the add provider button.
     */
    private fun configureAddButton() {
        binding.plFabAddProvider.setOnClickListener {
            ToolbarService.getInstance().detailTitle = getString(R.string.new_provider)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("fragmentType", "newProvider")
            startActivity(intent)
        }
    }

    /**
     * Retrieves the list of providers from Firebase.
     */
    private fun getProvidersList() {
        viewModel.getProvidersListFromFB()
    }

    /**
     * Loads the list of providers into the RecyclerView.
     */
    private fun loadProvidersListInRV() {
        providersAdapter = ProviderAdapter(providersList, requireContext())
        val providersGridFormat =
            ConfigService().getPreferencesBoolean(requireContext(), Constants.SETTINGS_PROVIDERS_GRID_FORMAT)
        if (providersGridFormat) {
            binding.plRvProviders.layoutManager = GridLayoutManager(requireContext(), 2)
        } else {
            binding.plRvProviders.layoutManager = LinearLayoutManager(requireContext())
        }
        binding.plRvProviders.adapter = providersAdapter
    }

    /**
     * Handles the result of the request for permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showSnackBar(getString(R.string.snackBar_callPermissionGranted), requireView()) {}
        } else {
            showSnackBar(getString(R.string.snackBar_noPermissions), requireView()) {}
        }
    }
}
