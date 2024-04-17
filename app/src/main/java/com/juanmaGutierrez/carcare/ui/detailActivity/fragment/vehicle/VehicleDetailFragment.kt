package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentVehicleDetailBinding
import com.juanmaGutierrez.carcare.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.service.showDialogAccept
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

class VehicleDetailFragment : Fragment() {
    private lateinit var binding: FragmentVehicleDetailBinding
    private lateinit var viewModel: VehicleDetailViewModel
    private lateinit var selectedCategory: String
    private lateinit var detailActivity: DetailActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[VehicleDetailViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVehicleDetailBinding.inflate(layoutInflater)
        detailActivity = activity as DetailActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllBrandsFromAPI()
        loadCategoriesInSelectable()
        viewModel.modelsList.observe(viewLifecycleOwner) { list -> loadModelsInSelectable(list) }
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message -> showSnackBar(message, requireView()) }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireView().findViewById<View>(R.id.vd_la_isLoading).visibility = View.VISIBLE
                false -> requireView().findViewById<View>(R.id.vd_la_isLoading).visibility = View.GONE
            }
        }
        checkActiveFragment()
        showInfoNewVehicle()
        binding.vdBtAccept.setOnClickListener {
            acceptVehicle()
        }
    }

    private fun acceptVehicle() {
        if (!checkAllFieldsValid()) {
            showSnackBar("falta rellenar algún campo", requireView())
            return
        }
        val ad = AlertDialogModel(
            this.requireActivity(),
            this.requireActivity().getString(R.string.alertDialog_show_info_newVehicle_title),
            this.requireActivity().getString(R.string.alertDialog_show_info_newVehicle_message)
        )
        showDialogAcceptCancel(ad) { aceptado ->
            if (aceptado) {
                showSnackBar("aceptar nuevo vehiculo", requireView())
            } else {
                showSnackBar("cancelar nuevo vehiculo", requireView())
            }
        }
    }

    private fun checkAllFieldsValid(): Boolean {
        val validEditText = checkValidEditText()
        val validDate = checkValidDate()
        return validEditText && validDate
    }

    private fun checkValidEditText(): Boolean {
        return listOf(
            binding.vdAcCategory, binding.vdAcBrand, binding.vdAcModel, binding.vdItPlate
        ).all { it.text.toString().isNotBlank() }
    }

    private fun checkValidDate(): Boolean {
        return true
    }

    private fun checkActiveFragment() {
        when (detailActivity.activeFragment) {
            "newVehicle" -> binding.vdBtDelete.visibility = View.GONE
            "editVehicle" -> showSnackBar("edición de vehículo", requireView())
        }
    }

    private fun showInfoNewVehicle() {
        val ad = AlertDialogModel(
            this.requireActivity(),
            this.requireActivity().getString(R.string.alertDialog_show_info_newVehicle_title),
            this.requireActivity().getString(R.string.alertDialog_show_info_newVehicle_message)
        )
        showDialogAccept(ad) { }
    }

    private fun loadCategoriesInSelectable() {
        val categorySelectable = binding.vdAcCategory
        val categoriesList = getCategories()
        val selectableAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, categoriesList)
        categorySelectable.setAdapter(selectableAdapter)
        categorySelectable.setOnItemClickListener { _, _, _, id ->
            val brandsSelectable = binding.vdAcBrand
            val modelsSelectable = binding.vdAcModel
            configureSelectables(brandsSelectable, modelsSelectable)
            when (categoriesList[id.toInt()]) {
                "Coche", "Car" -> {
                    selectedCategory = "car"
                    loadBrandsInSelectable(brandsSelectable, VehicleBrandsService.carsList)
                }

                "Motocicleta", "Motorcycle" -> {
                    selectedCategory = "motorcycle"
                    loadBrandsInSelectable(
                        brandsSelectable, VehicleBrandsService.motorcyclesList
                    )
                }

                "Furgoneta", "Van" -> {
                    selectedCategory = "van"
                    loadBrandsInSelectable(brandsSelectable, VehicleBrandsService.vansList)
                }

                "Camión", "Truck" -> {
                    selectedCategory = "truck"
                    loadBrandsInSelectable(brandsSelectable, VehicleBrandsService.trucksList)
                }
            }
            loadModelsByBrand()
        }
    }

    private fun loadModelsByBrand() {
        val brandSelectable = binding.vdAcBrand
        brandSelectable.setOnItemClickListener { _, _, _, id ->
            val modelsSelectable = binding.vdAcModel
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
        val modelsSelectable = binding.vdAcModel
        modelsSelectable.setText("")
        loadModelsInSelectable(emptyList())
    }

    private fun clearBrands() {
        val brandsSelectable = binding.vdAcBrand
        brandsSelectable.setText("")
        brandsSelectable.isEnabled = true
        loadBrandsInSelectable(brandsSelectable, emptyList())
    }

    private fun loadBrandsInSelectable(selectable: AutoCompleteTextView, listItems: List<String>) {
        val selectableAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, listItems)
        selectable.setAdapter(selectableAdapter)
    }

    private fun loadModelsInSelectable(modelsList: List<String>) {
        val modelSelectable = binding.vdAcModel
        val selectableAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, modelsList)
        modelSelectable.setAdapter(selectableAdapter)
    }

    private fun getCategories(): List<String> {
        return listOf(
            getString(R.string.vehicle_category_car),
            getString(R.string.vehicle_category_motorcycle),
            getString(R.string.vehicle_category_van),
            getString(R.string.vehicle_category_truck)
        )
    }
}