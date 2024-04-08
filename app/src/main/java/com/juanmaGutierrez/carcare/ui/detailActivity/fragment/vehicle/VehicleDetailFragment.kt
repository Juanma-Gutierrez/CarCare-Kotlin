package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.service.log
import com.juanmaGutierrez.carcare.service.showSnackBar

class VehicleDetailFragment : Fragment() {
    private lateinit var viewModel: VehicleDetailViewModel
    private lateinit var selectedCategory: String

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[VehicleDetailViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vehicle_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCategoriesInSelectable()
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
            showSnackBar("Pulsado: $vehicleRef", requireView())
            // val vehiclesBrandSVC = VehicleBrandsService
            viewModel.loadModelsByBrand(vehicleRef)
            viewModel.modelsList.observe(viewLifecycleOwner) { list ->
                loadModelsInSelectable(list)
            }
        }
    }

    // todo continuar con la carga de datos del modelo, ahora mismo crashea
    private fun loadModelsInSelectable(modelsList: List<String>) {
        val modelSelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_model)
        val selectableAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, modelsList)
        modelSelectable.setAdapter(selectableAdapter)
    }


    private fun loadCategoriesInSelectable() {
        val categorySelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_category)
        val categoriesList = createCategories()
        val selectableAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, categoriesList)
        categorySelectable.setAdapter(selectableAdapter)
        categorySelectable.setOnItemClickListener { _, _, _, id ->
            val brandsSelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_brand)
            clearBrands()
            clearModels()
            brandsSelectable.isEnabled = true
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


    private fun clearBrands() {
        val brandsSelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_brand)
        brandsSelectable.setText("")
        brandsSelectable.isEnabled = true
        loadBrandsInSelectable(brandsSelectable, emptyList())
    }

    private fun clearModels() {
        val modelSelectable: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_model)
        loadModelInSelectable(modelSelectable, emptyList())
    }

    private fun loadBrandsInSelectable(selectable: AutoCompleteTextView, listItems: List<String>) {
        val selectableAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, listItems)
        selectable.setAdapter(selectableAdapter)
        selectable.setOnItemClickListener { _, _, _, id ->
            showSnackBar(listItems[id.toInt()], requireView())
        }
    }

    private fun loadModelInSelectable(selectable: AutoCompleteTextView, listItems: List<String>) {
        val selectableAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, listItems)
        selectable.setAdapter(selectableAdapter)
        selectable.setOnItemClickListener { _, _, _, id ->
            showSnackBar(listItems[id.toInt()], requireView())
        }
    }

    private fun createCategories(): List<String> {
        return listOf(
            getString(R.string.vehicle_category_car),
            getString(R.string.vehicle_category_motorcycle),
            getString(R.string.vehicle_category_van),
            getString(R.string.vehicle_category_truck)
        )
    }

}