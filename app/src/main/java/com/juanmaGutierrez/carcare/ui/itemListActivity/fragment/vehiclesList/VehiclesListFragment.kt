package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList

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
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.showSnackBar
import kotlinx.coroutines.launch

class VehiclesListFragment : Fragment() {
    private lateinit var viewModel: VehiclesListViewModel
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var binding: FragmentVehiclesListBinding
    private lateinit var vehiclesList: List<VehiclePreview>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[VehiclesListViewModel::class.java]
        binding = FragmentVehiclesListBinding.inflate(layoutInflater)
        binding.vlFabAddVehicle.setOnClickListener {
            val ts = ToolbarService.getInstance()
            ts.detailTitle = getString(R.string.new_vehicle)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("fragmentType", "newVehicle")
            startActivity(intent)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadLocalVehicles(requireContext())
        viewModel.vehiclesList.observe(viewLifecycleOwner) { vehiclesList ->
            this.vehiclesList = vehiclesList
            checkSwitchAndUpdateRecylerView()
        }
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            showSnackBar(message, this.requireView())
        }
        configureSwitchAllVehicles()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveFBVehiclesToRoom()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireView().findViewById<View>(R.id.vd_la_isLoading).visibility = View.VISIBLE
                false -> requireView().findViewById<View>(R.id.vd_la_isLoading).visibility = View.GONE
            }
        }
    }

    private fun checkSwitchAndUpdateRecylerView() {
        val switch = binding.vlSwSwitchAllVehicles
        updateRecyclerView(this.vehiclesList, switch.isChecked)
    }

    private fun configureSwitchAllVehicles() {
        val switch = binding.vlSwSwitchAllVehicles
        switch.setOnCheckedChangeListener { _, _ ->
            if (switch.isChecked) {
                showSnackBar(getString(R.string.snackBar_showAll), requireView())
            } else {
                showSnackBar(getString(R.string.snackBar_showAvailables), requireView())
            }
            updateRecyclerView(this.vehiclesList, switch.isChecked)
        }
    }

    private fun updateRecyclerView(vehiclesList: List<VehiclePreview>?, switch: Boolean) {
        val filteredList = viewModel.filtercheckAvailablesVehicles(vehiclesList!!, switch)
        vehicleAdapter = VehicleAdapter(filteredList)
        vehicleAdapter.updateData(filteredList)
        binding.vlRvVehicles.layoutManager = LinearLayoutManager(requireContext())
        binding.vlRvVehicles.adapter = vehicleAdapter
    }
}