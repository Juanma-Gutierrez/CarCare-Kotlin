package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentVehicleEditBinding
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.service.formatDate
import com.juanmaGutierrez.carcare.service.showDatePickerDialog
import com.juanmaGutierrez.carcare.service.stringToTimestamp
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

class VehicleEditFragment : Fragment() {
    private lateinit var binding: FragmentVehicleEditBinding
    private lateinit var viewModel: VehicleEditViewModel
    private lateinit var selectedCategory: String
    private lateinit var detailActivity: DetailActivity
    private lateinit var itemID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVehicleEditBinding.inflate(layoutInflater)
        detailActivity = activity as DetailActivity
        viewModel = ViewModelProvider(this)[VehicleEditViewModel::class.java]
        itemID = arguments?.getString("itemID") ?: ""
        if (itemID != "") viewModel.init(itemID)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isLoadingVisible.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.VISIBLE
            } else {
                requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.GONE
            }
        })
        viewModel.vehicle.observe(viewLifecycleOwner, Observer { vehicle ->
            loadVehicleDataToForm(vehicle)
        })
        binding.veCbDate.setOnClickListener {
            showDatePickerDialog("01/02/2024", "Selecciona la fecha de registro", childFragmentManager)
        }
    }

    private fun loadVehicleDataToForm(vehicle: VehicleFB) {
        binding.veAcCategory.setText(vehicle.category, false)
        binding.veAcBrand.setText(vehicle.brand, false)
        binding.veAcModel.setText(vehicle.model, false)
        binding.veItPlate.setText(vehicle.plate)
        binding.veCbAvailable.isChecked = vehicle.available
        binding.veCbDate.text = vehicle.registrationDate.formatDate("dd/MM/yyyy")
// todo configurar fecha
    }
}