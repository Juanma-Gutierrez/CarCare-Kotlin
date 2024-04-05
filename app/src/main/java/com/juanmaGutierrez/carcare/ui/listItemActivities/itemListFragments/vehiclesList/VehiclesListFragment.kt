package com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments.vehiclesList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.ui.detailActivities.DetailActivity
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.model.Vehicle
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.showSnackBar
import kotlinx.coroutines.launch

class VehiclesListFragment : Fragment() {
    private lateinit var vehiclesListViewModel: VehiclesListViewModel
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var binding: FragmentVehiclesListBinding
    private lateinit var vehiclesList: List<Vehicle>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        vehiclesListViewModel = ViewModelProvider(this)[VehiclesListViewModel::class.java]
        binding = FragmentVehiclesListBinding.inflate(inflater, container, false)
        binding.veFabAddVehicle.setOnClickListener {
            val ts = ToolbarService.getInstance()
            ts.detailTitle = getString(R.string.new_vehicle)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vehiclesListViewModel.loadLocalVehicles(requireContext())
        vehiclesListViewModel.vehiclesList.observe(viewLifecycleOwner) { vehiclesList ->
            this.vehiclesList = vehiclesList
            checkSwitchAndUpdateRecylerView()
        }
        vehiclesListViewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            showSnackBar(message, this.requireView())
        }
        configureSwitchAllVehicles()
        viewLifecycleOwner.lifecycleScope.launch {
            vehiclesListViewModel.saveFBVehiclesToRoom()
        }
    }

    private fun checkSwitchAndUpdateRecylerView() {
        val switch = binding.veSwSwitchAllVehicles
        updateRecyclerView(this.vehiclesList, switch.isChecked)
    }

    private fun configureSwitchAllVehicles() {
        val switch = binding.veSwSwitchAllVehicles
        switch.setOnCheckedChangeListener { _, _ ->
            if (switch.isChecked) {
                showSnackBar(getString(R.string.snackBar_showAll), requireView())
            } else {
                showSnackBar(getString(R.string.snackBar_showAvailables), requireView())
            }
            updateRecyclerView(this.vehiclesList, switch.isChecked)
        }
    }

    private fun updateRecyclerView(vehiclesList: List<Vehicle>?, switch: Boolean) {
        val filteredList = vehiclesListViewModel.filtercheckAvailablesVehicles(vehiclesList!!, switch)
        vehicleAdapter = VehicleAdapter(filteredList)
        vehicleAdapter.updateData(filteredList)
        binding.veRvVehicles.layoutManager = LinearLayoutManager(requireContext())
        binding.veRvVehicles.adapter = vehicleAdapter
    }
}