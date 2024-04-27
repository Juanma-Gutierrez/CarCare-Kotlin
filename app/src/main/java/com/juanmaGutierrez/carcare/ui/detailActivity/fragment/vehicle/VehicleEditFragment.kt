package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentVehicleEditBinding
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.service.transformDateIsoToString
import com.juanmaGutierrez.carcare.service.log
import com.juanmaGutierrez.carcare.service.showDatePickerDialog
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.service.transformStringToDateIso
import com.juanmaGutierrez.carcare.service.translateCategory
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
        viewModel.isLoadingVisible.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.VISIBLE
            } else {
                requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.GONE
            }
        }
        viewModel.vehicle.observe(viewLifecycleOwner) { vehicle ->
            initVehicle(vehicle)
            log(vehicle.toString())
            binding.veBtAccept.setOnClickListener {
                acceptEditVehicle(vehicle)
            }
            binding.veBtDelete.setOnClickListener {
                deleteVehicle(vehicle)
            }
        }
        binding.veBtCancel.setOnClickListener {
            cancelEditVehicle()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initVehicle(vehicle: VehicleFB) {
        loadVehicleDataToForm(vehicle)
        binding.veCbDate.setOnClickListener {
            showDatePickerDialog(
                vehicle.registrationDate.transformDateIsoToString(Constants.LOCAL_DATE_FORMAT),
                requireActivity().getString(R.string.vehicle_editVehicle_calendarTitle),
                childFragmentManager
            ) { selectedDate ->
                binding.veCbDate.text = selectedDate
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadVehicleDataToForm(vehicle: VehicleFB) {
        binding.veAcCategory.setText(vehicle.category, false)
        binding.veAcBrand.setText(vehicle.brand, false)
        binding.veAcModel.setText(vehicle.model, false)
        binding.veItPlate.setText(vehicle.plate)
        binding.veCbAvailable.isChecked = vehicle.available
        binding.veCbDate.text = vehicle.registrationDate.transformDateIsoToString()
        log("en loadVehicleDataToForm: ${vehicle.registrationDate.transformDateIsoToString()}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun acceptEditVehicle(vehicle: VehicleFB) {
        log("aceptar")
        val editedVehicle: VehicleFB = getDataFromForm(vehicle)
        viewModel.editVehicle(editedVehicle)
        showSnackBar("Editado correctamente", requireView()) {
            closeFragment()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDataFromForm(v: VehicleFB): VehicleFB {
        val vehicle = VehicleFB(
            binding.veCbAvailable.isChecked,
            binding.veAcBrand.text.toString(),
            binding.veAcCategory.text.toString().translateCategory(),
            v.created,
            binding.veAcModel.text.toString(),
            binding.veItPlate.text.toString(),
            binding.veCbDate.text.toString().transformStringToDateIso(),
            v.spents,
            v.userId,
            v.vehicleId
        )
        return vehicle
    }

    private fun cancelEditVehicle() {
        closeFragment()
    }

    private fun deleteVehicle(v: VehicleFB) {
        log("borrar")
    }

    private fun closeFragment() {
        requireActivity().onBackPressed()
    }
}