package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentVehicleEditBinding
import com.juanmaGutierrez.carcare.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.localData.getCategories
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.service.getCategoryTranslation
import com.juanmaGutierrez.carcare.service.loadDataInSelectable
import com.juanmaGutierrez.carcare.service.log
import com.juanmaGutierrez.carcare.service.showDatePickerDialog
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.service.transformDateIsoToString
import com.juanmaGutierrez.carcare.service.transformStringToDateIso
import com.juanmaGutierrez.carcare.service.translateCategory

class VehicleEditFragment : Fragment() {
    private lateinit var binding: FragmentVehicleEditBinding
    private lateinit var viewModel: VehicleEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[VehicleEditViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVehicleEditBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getVehicleFromID()
        configureVehicle()
        configureSelectables()
        configureUI()
        configureCancelButton()
        configureEditVehicleSuccessful()
    }

    private fun getVehicleFromID() {
        val itemID = arguments?.getString("itemID") ?: ""
        if (itemID != "") {
            viewModel.getVehicleFromFB(itemID)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureVehicle() {
        viewModel.vehicle.observe(viewLifecycleOwner) { vehicle ->
            loadVehicleDataToForm(vehicle)
            viewModel.setCategories(getCategories(requireActivity()))
            viewModel.selectedCategory = binding.veAcCategory.text.toString().translateCategory()
            viewModel.getBrandsFromAPI(vehicle.category)
            viewModel.getModelsFromBrandAPI(vehicle.brand)
            configureDateButton(vehicle)
            configureVehicleButtons(vehicle)
        }
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureDateButton(vehicle: VehicleFB) {
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

    private fun configureSelectables() {
        configureSelectablesObservers()
        configureSelectablesActions()
    }

    private fun configureSelectablesObservers() {
        viewModel.categoriesList.observe(viewLifecycleOwner) { categoriesList ->
            loadDataInSelectable(binding.veAcCategory, categoriesList, requireActivity())
        }
        viewModel.brandsList.observe(viewLifecycleOwner) { brandsList ->
            loadDataInSelectable(binding.veAcBrand, brandsList, requireActivity())
        }
        viewModel.modelsList.observe(viewLifecycleOwner) { modelsList ->
            loadDataInSelectable(binding.veAcModel, modelsList, requireActivity())
        }
    }

    private fun configureSelectablesActions() {
        configureCategorySelectable()
        configureBrandSelectable()
    }

    private fun configureCategorySelectable() {
        val categorySelectable = binding.veAcCategory
        val categoriesList = getCategories(requireActivity())
        categorySelectable.setOnItemClickListener { _, _, _, id ->
            clearBrandSelectable()
            clearModelSelectable()
            when (categoriesList[id.toInt()]) {
                "Coche", "Car" -> {
                    viewModel.selectedCategory = "car"
                    loadDataInSelectable(binding.veAcBrand, VehicleBrandsService.carsList, requireActivity())
                }

                "Motocicleta", "Motorcycle" -> {
                    viewModel.selectedCategory = "motorcycle"
                    loadDataInSelectable(
                        binding.veAcBrand, VehicleBrandsService.motorcyclesList, requireActivity()
                    )
                }

                "Furgoneta", "Van" -> {
                    viewModel.selectedCategory = "van"
                    loadDataInSelectable(binding.veAcBrand, VehicleBrandsService.vansList, requireActivity())
                }

                "Camión", "Truck" -> {
                    viewModel.selectedCategory = "truck"
                    loadDataInSelectable(binding.veAcBrand, VehicleBrandsService.trucksList, requireActivity())
                }
            }
        }
    }

    private fun clearBrandSelectable() {
        binding.veAcBrand.setText("")
        binding.veAcModel.isEnabled = false
        loadDataInSelectable(binding.veAcModel, emptyList(), requireActivity())
    }

    private fun clearModelSelectable() {
        binding.veAcModel.setText("")
    }

    private fun configureBrandSelectable() {
        val brandSelectable = binding.veAcBrand
        brandSelectable.setOnItemClickListener { _, _, _, id ->
            clearModelSelectable()
            binding.veAcModel.isEnabled = true
            val vehicleRef = when (viewModel.selectedCategory) {
                "car" -> VehicleBrandsService.carsList[id.toInt()]
                "motorcycle" -> VehicleBrandsService.motorcyclesList[id.toInt()]
                "van" -> VehicleBrandsService.vansList[id.toInt()]
                "truck" -> VehicleBrandsService.trucksList[id.toInt()]
                else -> ""
            }
            viewModel.getModelsFromBrandAPI(vehicleRef)
        }
    }

    private fun configureUI() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.GONE
            }
        }
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message -> showSnackBar(message, requireView()) {} }
    }

    private fun configureCancelButton() {
        binding.veBtCancel.setOnClickListener {
            closeFragment()
        }
    }

    private fun configureEditVehicleSuccessful() {
        viewModel.editVehicleSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                showSnackBar(requireActivity().getString(R.string.vehicle_editVehicle_successfully), requireView()) {
                    closeFragment()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureVehicleButtons(vehicle: VehicleFB) {
        binding.veBtAccept.setOnClickListener {
            editVehicle(vehicle)
        }
        binding.veBtDelete.setOnClickListener {
            deleteVehicle(vehicle)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun editVehicle(vehicle: VehicleFB) {
        val ad = AlertDialogModel(
            this.requireActivity(),
            this.requireActivity().getString(R.string.alertDialog_editVehicle_title),
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

    private fun deleteVehicle(vehicle: VehicleFB) {
        val ad = AlertDialogModel(
            this.requireActivity(),
            this.requireActivity().getString(R.string.alertDialog_deleteVehicle_title),
            this.requireActivity().getString(R.string.alertDialog_deleteVehicle_message)
        )
        showDialogAcceptCancel(ad) { accept ->
            if (accept) {
                try {
                    acceptDeleteVehicle(vehicle)
                } catch (e: Exception) {
                    Log.e(Constants.TAG, Constants.ERROR_DATABASE, e)
                }
            }
        }
    }

    private fun acceptDeleteVehicle(vehicle: VehicleFB) {
        viewModel.deleteVehicle(vehicle)
        // delete preview
    }


    private fun closeFragment() {
        if (isAdded) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}
