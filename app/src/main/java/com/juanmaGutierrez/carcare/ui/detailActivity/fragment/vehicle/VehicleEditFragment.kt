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
import com.juanmaGutierrez.carcare.databinding.FragmentVehicleEditBinding
import com.juanmaGutierrez.carcare.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.service.getCategoryTranslation
import com.juanmaGutierrez.carcare.service.loadDataInSelectable
import com.juanmaGutierrez.carcare.service.transformDateIsoToString
import com.juanmaGutierrez.carcare.service.log
import com.juanmaGutierrez.carcare.service.showDatePickerDialog
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
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
        viewModel = ViewModelProvider(this)[VehicleEditViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVehicleEditBinding.inflate(layoutInflater)
        detailActivity = activity as DetailActivity
        itemID = arguments?.getString("itemID") ?: ""
        if (itemID != "") viewModel.init(itemID)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllBrandsFromAPI()
        loadCategoriesInSelectable()
        viewModel.modelsList.observe(viewLifecycleOwner) { list ->
            loadDataInSelectable(
                binding.veAcModel, list, requireActivity()
            )
        }
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message -> showSnackBar(message, requireView()) {} }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.GONE
            }
        }
        viewModel.vehicle.observe(viewLifecycleOwner) { vehicle ->
            initVehicle(vehicle)
            log(vehicle.toString())
            binding.veBtAccept.setOnClickListener {
                editVehicle(vehicle)
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
        initSelectables(vehicle)
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

    private fun initSelectables(vehicle: VehicleFB) {
        viewModel.getAllBrandsFromAPI()
        viewModel.loadModelsByBrand(vehicle.brand)
        // todo arreglar la carga de selectables, que no salen todos disponibles de inicio
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadVehicleDataToForm(vehicle: VehicleFB) {
        val category = vehicle.category.getCategoryTranslation(requireContext())
        binding.veAcCategory.setText(category, false)
        binding.veAcBrand.setText(vehicle.brand, false)
        binding.veAcModel.setText(vehicle.model, false)
        binding.veItPlate.setText(vehicle.plate)
        binding.veCbAvailable.isChecked = vehicle.available
        binding.veCbDate.text = vehicle.registrationDate.transformDateIsoToString()
        log("en loadVehicleDataToForm: ${vehicle.registrationDate.transformDateIsoToString()}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun editVehicle(vehicle: VehicleFB) {
        val ad = AlertDialogModel(
            this.requireActivity(),
            this.requireActivity().getString(R.string.alertDialog_confirm_message),
            this.requireActivity().getString(R.string.alertDialog_editVehicle_message)
        )
        showDialogAcceptCancel(ad) { accept ->
            if (accept) {
                try {
                    acceptEditVehicle(vehicle)
                } catch (e: Exception) {
                    Log.e(Constants.TAG, Constants.ERROR_DATABASE, e)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun acceptEditVehicle(vehicle: VehicleFB) {
        val editedVehicle: VehicleFB = getDataFromForm(vehicle)
        viewModel.editVehicle(editedVehicle)
        viewModel.editVehicleSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                showSnackBar(requireActivity().getString(R.string.vehicle_editVehicle_successfully), requireView()) {
                    closeFragment()
                }
            }
        }
    }

    private fun deleteVehicle(vehicle: VehicleFB) {
        viewModel.deleteVehicle(vehicle)
        log("borrar")
    }

    private fun cancelEditVehicle() {
        closeFragment()
    }

    private fun closeFragment() {
        this.requireActivity().onBackPressedDispatcher.onBackPressed()
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

    private fun configureSelectables(brandsSelectable: AutoCompleteTextView, modelsSelectable: AutoCompleteTextView) {
        brandsSelectable.isEnabled = true
        modelsSelectable.isEnabled = false
        clearBrands()
        clearModels()
    }

    private fun clearModels() {
        val modelsSelectable = binding.veAcModel
        modelsSelectable.setText("")
        loadDataInSelectable(modelsSelectable, emptyList(), requireActivity())
    }

    private fun clearBrands() {
        val brandsSelectable = binding.veAcBrand
        brandsSelectable.setText("")
        brandsSelectable.isEnabled = true
        loadDataInSelectable(brandsSelectable, emptyList(), requireActivity())
    }

    private fun getCategories(): List<String> {
        return listOf(
            getString(R.string.vehicle_category_car),
            getString(R.string.vehicle_category_motorcycle),
            getString(R.string.vehicle_category_van),
            getString(R.string.vehicle_category_truck)
        )
    }

    private fun loadCategoriesInSelectable() {
        val categorySelectable = binding.veAcCategory
        val categoriesList = getCategories()
        val selectableAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, categoriesList)
        categorySelectable.setAdapter(selectableAdapter)
        categorySelectable.setOnItemClickListener { _, _, _, id ->
            val brandsSelectable = binding.veAcBrand
            val modelsSelectable = binding.veAcModel
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
        val brandSelectable = binding.veAcBrand
        brandSelectable.setOnItemClickListener { _, _, _, id ->
            val modelsSelectable = binding.veAcModel
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
}