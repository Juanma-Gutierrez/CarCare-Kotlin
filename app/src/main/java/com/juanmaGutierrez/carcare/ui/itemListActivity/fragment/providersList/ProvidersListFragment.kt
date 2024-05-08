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
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity


class ProvidersListFragment : Fragment() {
    private lateinit var viewModel: ProvidersListViewModel
    private lateinit var binding: FragmentProvidersListBinding
    private lateinit var providersAdapter: ProviderAdapter
    private lateinit var providersList: List<Provider>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        configureUI()
        configureAddButton()
        getProvidersList()
        loadProviderInRV()
        return binding.root
    }

    private fun configureUI() {
        viewModel = ViewModelProvider(this)[ProvidersListViewModel::class.java]
        binding = FragmentProvidersListBinding.inflate(layoutInflater)
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
        providersList = listOf(
            Provider("Categoría 1", "2024-05-08", "Proveedor 1", "123456789", "id1"),
            Provider("Categoría 2", "2024-05-08", "Proveedor 2", "987654321", "id2"),
            Provider("Categoría 3", "2024-05-08", "Proveedor 3", "456123789", "id3"),
            Provider("Categoría 1", "2024-05-08", "Proveedor 4", "789456123", "id4"),
            Provider("Categoría 2", "2024-05-08", "Proveedor 5", "321654987", "id5")
        )
    }

    private fun loadProviderInRV() {
        providersAdapter = ProviderAdapter(providersList, requireContext())
        binding.plRvProviders.layoutManager = LinearLayoutManager(requireContext())
        binding.plRvProviders.adapter = providersAdapter
    }
}
