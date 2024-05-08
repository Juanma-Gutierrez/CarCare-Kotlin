package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.providersList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.ProviderAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentProvidersListBinding
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity


class ProvidersListFragment : Fragment() {
    private lateinit var viewModel: ProvidersListViewModel
    private lateinit var binding: FragmentProvidersListBinding
    private lateinit var providersAdapter: ProviderAdapter
    private var providersList: List<Provider> = emptyList()


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

    private fun configureUI() {
        viewModel = ViewModelProvider(this)[ProvidersListViewModel::class.java]
        binding = FragmentProvidersListBinding.inflate(layoutInflater)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
            }
        }
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message -> showSnackBar(message, requireView()) {} }
    }

    private fun configureObservers() {
        viewModel.providersList.observe(viewLifecycleOwner) { providers ->
            providersList = providers
            loadProvidersListInRV()
        }
    }

    private fun configureAddButton() {
        binding.plFabAddProvider.setOnClickListener {
            ToolbarService.getInstance().detailTitle = getString(R.string.new_provider)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("fragmentType", "newProvider")
            startActivity(intent)
        }
    }

    private fun getProvidersList() {
        viewModel.getProvidersListFromFB()
    }

    private fun loadProvidersListInRV() {
        providersAdapter = ProviderAdapter(providersList, requireContext())
        binding.plRvProviders.layoutManager = LinearLayoutManager(requireContext())
        binding.plRvProviders.adapter = providersAdapter
    }
}
