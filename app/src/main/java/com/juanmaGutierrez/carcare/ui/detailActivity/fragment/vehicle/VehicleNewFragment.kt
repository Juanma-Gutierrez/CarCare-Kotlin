package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentVehicleNewBinding
import com.juanmaGutierrez.carcare.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.localData.getCategories
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.service.convertDateMillisToDate
import com.juanmaGutierrez.carcare.service.generateId
import com.juanmaGutierrez.carcare.service.loadDataInSelectable
import com.juanmaGutierrez.carcare.service.saveToLog
import com.juanmaGutierrez.carcare.service.showDialogAccept
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.service.translateCategory
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity
import java.util.Date

class VehicleNewFragment : Fragment() {
    private lateinit var binding: FragmentVehicleNewBinding
    private lateinit var viewModel: VehicleNewViewModel
    private lateinit var selectedCategory: String
    private lateinit var detailActivity: DetailActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[VehicleNewViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVehicleNewBinding.inflate(layoutInflater)
        detailActivity = activity as DetailActivity
        showInfoNewVehicle()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllBrandsFromAPI()
        loadCategoriesInSelectable()
        viewModel.modelsList.observe(viewLifecycleOwner) { list ->
            loadDataInSelectable(
                binding.vnAcModel, list, requireActivity()
            )
        }
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message -> showSnackBar(message, requireView()) {} }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.GONE
            }
        }
        binding.vnBtAccept.setOnClickListener {
            acceptVehicle()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun acceptVehicle() {
        if (!checkAllFieldsValid()) {
            showSnackBar("falta rellenar algún campo", requireView()) { }
            return
        }
        val ad = AlertDialogModel(
            this.requireActivity(),
            this.requireActivity().getString(R.string.alertDialog_newVehicle_title),
            this.requireActivity().getString(R.string.alertDialog_newVehicle_message)
        )
        showDialogAcceptCancel(ad) { accept ->
            if (accept) {
                try {
                    val vehicle: VehicleFB = generateVehicle()
                    viewModel.saveVehicleAndVehiclePreview(vehicle)
                    val message = requireActivity().getString(R.string.vehicle_createVehicle_successfully)
                    saveToLog(LogType.INFO, OperationLog.VEHICLE, message)
                    navigateToVehiclesList()
                } catch (e: Error) {
                    Log.e(Constants.TAG_ERROR, "Error in vehicle creation: ${e.message}")
                }
            } else {
                val message = requireActivity().getString(R.string.vehicle_createVehicle_error)
                showSnackBar(message, requireView()) {}
                saveToLog(LogType.ERROR, OperationLog.VEHICLE, message)
            }
        }
    }

    private fun navigateToVehiclesList() {
        this.requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateVehicle(): VehicleFB {
        val fb = FirebaseService.getInstance()
        return VehicleFB(
            binding.vnCbAvailable.isChecked,
            binding.vnAcBrand.text.toString(),
            binding.vnAcCategory.text.toString().translateCategory(),
            Date().time.toString().convertDateMillisToDate(),
            binding.vnAcModel.text.toString(),
            binding.vnItPlate.text.toString(),
            binding.vnCvRegistrationDate.date.toString().convertDateMillisToDate(),
            emptyList(),
            fb.user!!.uid,
            generateId()
        )
    }

    private fun checkAllFieldsValid(): Boolean {
        val validEditText = checkValidEditText()
        val validDate = checkValidDate()
        return validEditText && validDate
    }

    private fun checkValidEditText(): Boolean {
        return listOf(
            binding.vnAcCategory, binding.vnAcBrand, binding.vnAcModel, binding.vnItPlate
        ).all { it.text.toString().isNotBlank() }
    }

    private fun checkValidDate(): Boolean {
        return true
    }

    private fun loadCategoriesInSelectable() {
        val categorySelectable = binding.vnAcCategory
        val categoriesList = getCategories(requireActivity())
        val selectableAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, categoriesList)
        categorySelectable.setAdapter(selectableAdapter)
        categorySelectable.setOnItemClickListener { _, _, _, id ->
            val brandsSelectable = binding.vnAcBrand
            val modelsSelectable = binding.vnAcModel
            configureSelectables(brandsSelectable, modelsSelectable)
            when (categoriesList[id.toInt()]) {
                "Coche", "Car" -> {
                    selectedCategory = "car"
                    loadDataInSelectable(brandsSelectable, VehicleBrandsService.carsList, requireActivity())
                }

                "Motocicleta", "Motorcycle" -> {
                    selectedCategory = "motorcycle"
                    loadDataInSelectable(
                        brandsSelectable, VehicleBrandsService.motorcyclesList, requireActivity()
                    )
                }

                "Furgoneta", "Van" -> {
                    selectedCategory = "van"
                    loadDataInSelectable(brandsSelectable, VehicleBrandsService.vansList, requireActivity())
                }

                "Camión", "Truck" -> {
                    selectedCategory = "truck"
                    loadDataInSelectable(brandsSelectable, VehicleBrandsService.trucksList, requireActivity())
                }
            }
            loadModelsByBrand()
        }
    }

    private fun loadModelsByBrand() {
        val brandSelectable = binding.vnAcBrand
        brandSelectable.setOnItemClickListener { _, _, _, id ->
            val modelsSelectable = binding.vnAcModel
            clearModels()
            modelsSelectable.isEnabled = true
            val vehicleRef = when (selectedCategory) {
                "car" -> VehicleBrandsService.carsList[id.toInt()]
                "motorcycle" -> VehicleBrandsService.motorcyclesList[id.toInt()]
                "van" -> VehicleBrandsService.vansList[id.toInt()]
                "truck" -> VehicleBrandsService.trucksList[id.toInt()]
                else -> ""
            }
            viewModel.selectedCategory = selectedCategory
            viewModel.loadModelsByBrand(vehicleRef)
        }
    }

    private fun configureSelectables(brandsSelectable: AutoCompleteTextView, modelsSelectable: AutoCompleteTextView) {
        brandsSelectable.isEnabled = true
        modelsSelectable.isEnabled = false
        clearBrands()
        clearModels()
    }

    private fun clearModels() {
        val modelsSelectable = binding.vnAcModel
        modelsSelectable.setText("")
        loadDataInSelectable(modelsSelectable, emptyList(), requireActivity())
    }

    private fun clearBrands() {
        val brandsSelectable = binding.vnAcBrand
        brandsSelectable.setText("")
        brandsSelectable.isEnabled = true
        loadDataInSelectable(brandsSelectable, emptyList(), requireActivity())
    }

    private fun showInfoNewVehicle() {
        val ad = AlertDialogModel(
            this.requireActivity(),
            this.requireActivity().getString(R.string.alertDialog_show_info_newVehicle_title),
            this.requireActivity().getString(R.string.alertDialog_show_info_newVehicle_message)
        )
        showDialogAccept(ad) { }
    }
}