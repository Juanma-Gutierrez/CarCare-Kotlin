package com.juanmaGutierrez.carcare.ui.listItemActivities.itemListFragments.vehiclesList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.ui.detailActivities.DetailActivity
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.showSnackBar

class VehiclesListFragment : Fragment() {
    private lateinit var vehiclesListViewModel: VehiclesListViewModel
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var binding: FragmentVehiclesListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        vehiclesListViewModel.addVehiclesWithDelay()
        // vehiclesListViewModel.loadLocalVehicles()

        vehiclesListViewModel.vehiclesList.observe(viewLifecycleOwner) { list ->
            Log.d("wanma", "Tamaño de la lista de vehículos ${list.size}")
        }

        configureSwitchAllVehicles()


        // getLocalVehiclesFromRoom()  // da bloqueo total
    }

    /*
        private fun getLocalVehiclesFromRoom() {
            CoroutineScope(Dispatchers.IO).launch {
                val localVehicles = vehiclesListViewModel.getLocalVehicles()
                CoroutineScope(Dispatchers.Main).launch {
                    // recyclerView.adapter = YourAdapter(localVehicles)
                    Log.d("wanme", "Carga de vehiculos de ROOM ${localVehicles.size}")
                }
            }
        }*/


    private fun configureSwitchAllVehicles() {
        val switch = binding.veSwSwitchAllVehicles
        // setupRecyclerView(switch.isChecked)
        switch.setOnCheckedChangeListener { _, _ ->
            if (switch.isChecked) {
                showSnackBar(getString(R.string.snackBar_showAll), requireView())
            } else {
                showSnackBar(getString(R.string.snackBar_showAvailables), requireView())
            }
            updateRecyclerView(switch.isChecked)
        }
    }

    private fun updateRecyclerView(switch: Boolean) {
        Log.d("wanma", "entra en setupRecyclerView $switch")
        Log.d("wanma", "Lista de vehículos: ${vehiclesListViewModel.vehiclesList.value!!.size}")

        /*
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
}*/
    }


}