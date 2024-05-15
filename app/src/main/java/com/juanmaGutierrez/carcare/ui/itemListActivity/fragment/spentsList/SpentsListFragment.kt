package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.carousel.CarouselLayoutManager
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.OnVehicleClickListener
import com.juanmaGutierrez.carcare.adapter.ProviderAdapter
import com.juanmaGutierrez.carcare.adapter.SpentAdapter
import com.juanmaGutierrez.carcare.adapter.VehicleInSpentsListAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentSpentsListBinding
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.milog
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

class SpentsListFragment : Fragment(), OnVehicleClickListener {
    private lateinit var viewModel: SpentsListViewModel
    private lateinit var binding: FragmentSpentsListBinding
    private lateinit var vehiclesAdapter: VehicleInSpentsListAdapter
    private lateinit var spentsAdapter: SpentAdapter

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

    override fun onVehicleClick(vehicle: VehiclePreview) {
        viewModel.vehicleClicked(vehicle)
    }

    private fun getVehiclesFromFB() {
        viewModel.getVehiclesListFromFB()
    }

    private fun configureUI() {
        configureFabButton()
    }

    private fun configureObservers() {
        configureVehicleListObserver()
        configureSelectedVehicleTitleObserver()
        configureNumSpentsObserver()
        configureTotalSpentsObserver()
        configureSpentsListObserver()
    }

    private fun configureVehicleListObserver() {
        viewModel.vehicles.observe(viewLifecycleOwner) { vehicles ->
            loadVehiclesInRV(vehicles)
        }
    }

    private fun configureSelectedVehicleTitleObserver() {
        viewModel.selectedVehicle.observe(viewLifecycleOwner) { vehicle ->
            binding.slTvVehicleSelected.text = "${vehicle.brand} ${vehicle.model}"
        }
    }

    private fun configureNumSpentsObserver() {
        viewModel.numSpents.observe(viewLifecycleOwner) { numSpents ->
            binding.slTvNumSpents.text = getString(R.string.spents_numSpents, numSpents)
        }
    }

    private fun configureTotalSpentsObserver() {
        viewModel.totalSpents.observe(viewLifecycleOwner) { totalSpents ->
            binding.slTvTotalSpents.text = getString(R.string.spents_totalSpents, totalSpents)
        }
    }

    private fun configureSpentsListObserver() {
        viewModel.spentsList.observe(viewLifecycleOwner) { spents ->
            loadSpentsInRV(spents)
        }
    }

    private fun loadSpentsInRV(spentsList: List<SpentFB>) {
        spentsAdapter = SpentAdapter(spentsList, requireContext())
        binding.slRvSpents.layoutManager = LinearLayoutManager(requireContext())
        binding.slRvSpents.adapter = spentsAdapter
    }

    private fun loadVehiclesInRV(vehicles: List<VehiclePreview>) {
        vehiclesAdapter = VehicleInSpentsListAdapter(vehicles, requireContext())
        vehiclesAdapter.setOnVehicleClickListener(this)
        binding.slRvVehiclesInSpentList.setLayoutManager(CarouselLayoutManager())
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
