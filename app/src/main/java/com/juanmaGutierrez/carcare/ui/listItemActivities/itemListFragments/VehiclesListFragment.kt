package com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.localData.AppDatabase
import com.juanmaGutierrez.carcare.localData.VehicleEntity
import com.juanmaGutierrez.carcare.service.showSnackBar
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
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val activity = requireActivity() as AppCompatActivity
        val appDatabase = AppDatabase.getInstance(activity.applicationContext)
        val vehicleDao = appDatabase.vehicleDao()
        GlobalScope.launch(Dispatchers.Main) {
            // val vehicles = vehicleDao.getVehicles()
            // val vehiclesList = viewModel.loadVehiclesFromRoom(requireActivity() as AppCompatActivity)
            val vehiclesList = vehicleDao.getVehicles()
            Log.d("wanma", "vehiclesLista en setup: $vehiclesList")
            // viewModel._vehicleList.value!!
            vehicleAdapter = VehicleAdapter(vehiclesList)
            Log.d("wanma", "setupRecyclerView: $vehiclesList")
            vehicleAdapter.updateData(vehiclesList)
            binding.veRvVehicles.layoutManager = LinearLayoutManager(requireContext())
            binding.veRvVehicles.adapter = vehicleAdapter
        }
    }

    private fun observeVehicleList() {
        viewModel.vehicleList.observe(viewLifecycleOwner) { vehicles ->
            Log.d("wanma", "observeVehicleList: $vehicles")
            vehicleAdapter.updateData(vehicles)
        }
    }

}