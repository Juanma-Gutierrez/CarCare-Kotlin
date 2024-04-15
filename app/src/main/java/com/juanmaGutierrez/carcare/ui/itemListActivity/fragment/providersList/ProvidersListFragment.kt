package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.providersList

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentProvidersListBinding
import com.juanmaGutierrez.carcare.databinding.FragmentSpentsListBinding
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.log
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity
import com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList.SpentsListViewModel


class ProvidersListFragment : Fragment() {
    private lateinit var viewModel: ProvidersListViewModel
    private lateinit var binding: FragmentProvidersListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ProvidersListViewModel::class.java]
        binding = FragmentProvidersListBinding.inflate(layoutInflater)
        binding.plFabAddProvider.setOnClickListener {
            val ts = ToolbarService.getInstance()
            ts.detailTitle = getString(R.string.new_provider)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("fragmentType", "newProvider")
            startActivity(intent)
        }
        return binding.root    }
}
