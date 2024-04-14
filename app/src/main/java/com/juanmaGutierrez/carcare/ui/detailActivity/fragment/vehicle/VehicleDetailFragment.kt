package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentVehicleDetailBinding
import com.juanmaGutierrez.carcare.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.Vehicle
import com.juanmaGutierrez.carcare.service.log
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

class VehicleDetailFragment : Fragment() {
    private lateinit var viewModel: VehicleDetailViewModel
    private lateinit var selectedCategory: String
    private lateinit var detailActivity: DetailActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[VehicleDetailViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        detailActivity = activity as DetailActivity
        return inflater.inflate(R.layout.fragment_vehicle_detail, container, false)
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
    }

    private fun checkActiveFragment() {
        if (detailActivity.activeFragment.equals("newVehicle")){
            requireView().findViewById<MaterialButton>(R.id.vd_bt_delete).visibility = View.GONE
        }
    }

    private fun showInfoNewVehicle() {
        val activity = this.requireActivity()
        MaterialAlertDialogBuilder(activity)
            .setTitle(activity.getString(R.string.show_info_newVehicle_title))
            .setMessage(activity.getString(R.string.show_info_newVehicle_message))
            .setPositiveButton(activity.getString(R.string.accept)) { _, _ -> }
            .show()
    }

    private fun loadCategoriesInSelectable() {
        val categorySelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_category)
        val categoriesList = getCategories()
        val selectableAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, categoriesList)
        categorySelectable.setAdapter(selectableAdapter)
        categorySelectable.setOnItemClickListener { _, _, _, id ->
            val brandsSelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_brand)
            val modelsSelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_model)
            configureSelectables(brandsSelectable, modelsSelectable)
            when (categoriesList[id.toInt()]) {
                "Coche", "Car" -> {
                    selectedCategory = "car"
                    loadBrandsInSelectable(brandsSelectable, VehicleBrandsService.carsList)
                }

                "Motocicleta", "Motorcycle" -> {
                    selectedCategory = "motorcycle"
                    loadBrandsInSelectable(
                        brandsSelectable,
                        VehicleBrandsService.motorcyclesList
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
        val brandSelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_brand)
        brandSelectable.setOnItemClickListener { _, _, _, id ->
            val modelsSelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_model)
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
        val modelsSelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_model)
        modelsSelectable.setText("")
        loadModelsInSelectable(emptyList())
    }

    private fun clearBrands() {
        val brandsSelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_brand)
        brandsSelectable.setText("")
        brandsSelectable.isEnabled = true
        loadBrandsInSelectable(brandsSelectable, emptyList())
    }

    private fun loadBrandsInSelectable(selectable: AutoCompleteTextView, listItems: List<String>) {
        val selectableAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, listItems)
        selectable.setAdapter(selectableAdapter)
        selectable.setOnItemClickListener { _, _, _, id ->
            showSnackBar(listItems[id.toInt()], requireView())
        }
    }

    private fun loadModelsInSelectable(modelsList: List<String>) {
        val modelSelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_model)
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

    private fun setIsLoading(s: String) {
        when (s) {

        }
    }
}