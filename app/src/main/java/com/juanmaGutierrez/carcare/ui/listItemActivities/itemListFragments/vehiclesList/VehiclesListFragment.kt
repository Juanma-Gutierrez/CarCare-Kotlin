package com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments.vehiclesList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.ui.detailActivities.DetailActivity
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.localData.AppDatabase
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.listItemActivities.viewModel.ItemListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VehiclesListFragment : Fragment() {
    private val vehiclesListViewModel: VehiclesListViewModel by viewModels()
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var binding: FragmentVehiclesListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        val switch = binding.veSwSwitchAllVehicles
        setupRecyclerView(switch.isChecked)
        switch.setOnCheckedChangeListener { _, _ ->
            if (switch.isChecked) {
                showSnackBar(getString(R.string.snackBar_showAll), requireView())
            } else {
                showSnackBar(getString(R.string.snackBar_showAvailables), requireView())
            }
            setupRecyclerView(switch.isChecked)
        }
        observeVehicleList()
    }

    private fun setupRecyclerView(switch: Boolean) {
        val activity = requireActivity() as AppCompatActivity
        val appDatabase = AppDatabase.getInstance(activity.applicationContext)
        val vehicleDao = appDatabase.vehicleDao()
        GlobalScope.launch(Dispatchers.Main) {
            val vehiclesList = vehicleDao.getVehicles()
            val filteredList = vehiclesListViewModel.filtercheckAvailablesVehicles(vehiclesList, switch)
            vehicleAdapter = VehicleAdapter(filteredList)
            vehicleAdapter.updateData(filteredList)
            binding.veRvVehicles.layoutManager = LinearLayoutManager(requireContext())
            binding.veRvVehicles.adapter = vehicleAdapter
        }
    }

    private fun observeVehicleList() {
        vehiclesListViewModel.vehicleList.observe(viewLifecycleOwner) { vehicles ->
            // vehicleAdapter.updateData(vehicles)
            Log.d("wanma", "Cambiado la lista de vehiculos ${vehicles.size} $vehicles")
        }
    }

}