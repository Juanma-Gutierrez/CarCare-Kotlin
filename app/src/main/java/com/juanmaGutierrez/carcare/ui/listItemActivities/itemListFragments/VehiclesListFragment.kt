package com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.localData.AppDatabase
import com.juanmaGutierrez.carcare.ui.listItemActivities.viewModel.ItemListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VehiclesListFragment : Fragment() {
    private val viewModel: ItemListViewModel by viewModels()
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var binding: FragmentVehiclesListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVehiclesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeVehicleList()
        val switch = binding.veSwSwitchAllVehicles
        setupRecyclerView(switch.isChecked)
        switch.setOnCheckedChangeListener { _, _ -> setupRecyclerView(switch.isChecked) }
    }

    private fun setupRecyclerView(switch:Boolean) {
        val activity = requireActivity() as AppCompatActivity
        val appDatabase = AppDatabase.getInstance(activity.applicationContext)
        val vehicleDao = appDatabase.vehicleDao()
        GlobalScope.launch(Dispatchers.Main) {
            val vehiclesList = vehicleDao.getVehicles()
            val filteredList = viewModel.filtercheckAvailablesVehicles(vehiclesList, switch)
            vehicleAdapter = VehicleAdapter(filteredList)
            vehicleAdapter.updateData(filteredList)
            binding.veRvVehicles.layoutManager = LinearLayoutManager(requireContext())
            binding.veRvVehicles.adapter = vehicleAdapter
        }
    }

    private fun observeVehicleList() {
        viewModel.vehicleList.observe(viewLifecycleOwner) { vehicles ->
            vehicleAdapter.updateData(vehicles)
        }
    }

}