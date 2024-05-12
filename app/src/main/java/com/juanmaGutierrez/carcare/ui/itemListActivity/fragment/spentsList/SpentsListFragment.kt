package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.ProviderAdapter
import com.juanmaGutierrez.carcare.adapter.VehicleInSpentsListAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentSpentsListBinding
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.milog
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

class SpentsListFragment : Fragment() {
    private lateinit var viewModel: SpentsListViewModel
    private lateinit var binding: FragmentSpentsListBinding
    private lateinit var vehiclesAdapter: VehicleInSpentsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SpentsListViewModel::class.java]
        binding = FragmentSpentsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getVehiclesFromFB()
        configureUI()
        configureObservers()
    }

    private fun getVehiclesFromFB() {
        milog("obtener vehiculos")
        viewModel.getVehiclesListFromFB()
    }

    private fun configureUI() {
        milog("configurar UI")
        configureFabButton()
    }

    private fun configureObservers() {
        milog("configurar observers")
        configureVehicleListObserver()
    }

    private fun configureVehicleListObserver() {
        viewModel.vehicles.observe(viewLifecycleOwner) { vehicles ->
            loadVehiclesInRV(vehicles)
        }
    }

    private fun loadVehiclesInRV(vehicles: List<VehiclePreview>) {
        vehiclesAdapter = VehicleInSpentsListAdapter(vehicles, requireContext())
        binding.slRvVehiclesInSpentList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.slRvVehiclesInSpentList.adapter = vehiclesAdapter
    }

    private fun configureFabButton() {
        binding.slFabAddSpent.setOnClickListener {
            val ts = ToolbarService.getInstance()
            ts.detailTitle = getString(R.string.new_spent)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("fragmentType", "newSpent")
            startActivity(intent)
        }
    }
}
