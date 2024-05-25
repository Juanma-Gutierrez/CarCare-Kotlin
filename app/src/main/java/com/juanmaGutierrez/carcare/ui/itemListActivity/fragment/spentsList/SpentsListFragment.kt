package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.carousel.CarouselLayoutManager
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.OnVehicleClickListener
import com.juanmaGutierrez.carcare.adapter.SpentAdapter
import com.juanmaGutierrez.carcare.adapter.VehicleInSpentsListAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentSpentsListBinding
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.model.localData.SpentByProviderForChart
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.toCapitalizeString
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
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
        checkIfVehicleSelected()
        configureUI()
        configureObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setIsLoading(false)
    }

    private fun checkIfVehicleSelected() {
        val vehicleId = arguments?.getString("vehicleId")
        vehicleId?.let { viewModel.vehicleSelectedById(it) }
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
        configureIsLoadingObserver()
    }


    private fun configureVehicleListObserver() {
        viewModel.vehicles.observe(viewLifecycleOwner) { vehicles ->
            loadVehiclesInRV(vehicles)
        }
    }

    private fun configureSelectedVehicleTitleObserver() {
        viewModel.selectedVehicle.observe(viewLifecycleOwner) { vehicle ->
            binding.slTvVehicleSelected.text = String.format("%1s, %2s", vehicle.brand, vehicle.model)
            binding.slFabAddSpent.visibility = View.VISIBLE
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureSpentsListObserver() {
        viewModel.spentsList.observe(viewLifecycleOwner) { spents ->
            loadSpentsInRV(spents)
            loadSpentsInChart(spents)
        }
    }

    private fun configureIsLoadingObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
            }
        }
    }

    private fun loadSpentsInRV(spentsList: List<SpentFB>) {
        spentsAdapter = SpentAdapter(spentsList, requireContext(), viewModel.selectedVehicle.value!!.vehicleId)
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
        binding.slFabAddSpent.visibility = View.GONE
        binding.slFabAddSpent.setOnClickListener {
            val ts = ToolbarService.getInstance()
            ts.detailTitle = getString(R.string.new_spent)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("fragmentType", "newSpent")
            intent.putExtra("vehicleId", viewModel.selectedVehicle.value!!.vehicleId)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadSpentsInChart(spents: List<SpentFB>) {
        val chartSize = viewModel.getChartSize(requireContext())
        if (chartSize > 1) {
            binding.slBcSpentsChart.show(viewModel.generateChart(spents, requireContext(), chartSize))
        } else {
            binding.slBcSpentsChart.visibility = View.GONE
        }
    }
}
