package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

class VehiclesListFragment : Fragment() {
    private lateinit var binding: FragmentVehiclesListBinding
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var vehiclesList: List<VehiclePreview>
    private lateinit var viewModel: VehiclesListViewModel
    private var step = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        step = 0
        viewModel = ViewModelProvider(this)[VehiclesListViewModel::class.java]
        binding = FragmentVehiclesListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        step = 0
        binding.vlTlAvailables.getTabAt(0)?.select()
        viewModel.getFBVehiclesAndSaveFBVehiclesToRoom()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureVehicles()
        configureUI()
        viewModel.getFBVehiclesAndSaveFBVehiclesToRoom()
    }

    private fun configureVehicles() {
        viewModel.loadLocalVehicles(requireContext())
        viewModel.vehiclesList.observe(viewLifecycleOwner) { vehiclesList ->
            this.vehiclesList = vehiclesList
            checkSwitchAndUpdateRecylerView()
            step++
            if (step >= 3 && vehiclesList.isEmpty()) {
                showSnackBar(getString(R.string.vehiclesList_noVehicles), requireView()) {}
            }
        }
    }

    private fun configureUI() {
        binding.vlFabAddVehicle.setOnClickListener {
            val ts = ToolbarService.getInstance()
            ts.detailTitle = getString(R.string.new_vehicle)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("fragmentType", "newVehicle")
            startActivity(intent)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
            }
        }
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            showSnackBar(message, this.requireView()) {}
        }
    }

    private fun checkSwitchAndUpdateRecylerView() {
        val tab = binding.vlTlAvailables
        updateRecyclerView(vehiclesList, false)
        tab.addOnTabSelectedListener(object : OnTabSelectedListener {

            override fun onTabSelected(tab: Tab?) {
                when (tab?.position) {
                    0 -> updateRecyclerView(vehiclesList, false)
                    1 -> updateRecyclerView(vehiclesList, true)
                    else -> updateRecyclerView(vehiclesList, false)
                }
            }

            override fun onTabReselected(tab: Tab?) {
                // not implemented
            }

            override fun onTabUnselected(tab: Tab?) {
                // not implemented
            }
        })
    }

    private fun updateRecyclerView(vehiclesList: List<VehiclePreview>?, switch: Boolean) {
        val filteredList = viewModel.filtercheckAvailablesVehicles(vehiclesList!!, switch)
        vehicleAdapter = VehicleAdapter(filteredList, requireContext())
        vehicleAdapter.updateData(filteredList)
        binding.vlRvVehicles.layoutManager = LinearLayoutManager(requireContext())
        binding.vlRvVehicles.adapter = vehicleAdapter
    }
}