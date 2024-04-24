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
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentVehicleNewBinding
import com.juanmaGutierrez.carcare.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.model.localData.LogType
import com.juanmaGutierrez.carcare.model.localData.OperationLog
import com.juanmaGutierrez.carcare.service.FirebaseService
import com.juanmaGutierrez.carcare.service.convertDateMillisToDate
import com.juanmaGutierrez.carcare.service.fbSetVehicle
import com.juanmaGutierrez.carcare.service.generateId
import com.juanmaGutierrez.carcare.service.log
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
    private lateinit var itemID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[VehicleNewViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVehicleNewBinding.inflate(layoutInflater)
        detailActivity = activity as DetailActivity
        checkNewOrEdit()
        return binding.root
    }

    private fun checkNewOrEdit() {
        itemID = arguments?.getString("itemID") ?: ""
        if (itemID != "") {
            val db = Firebase.firestore
            val docRef = db.collection(Constants.FB_COLLECTION_VEHICLE).document(itemID)
            requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.VISIBLE
            docRef.get().addOnSuccessListener { document ->
                if (document.data != null) {
                    requireActivity().findViewById<View>(R.id.de_la_isLoading).visibility = View.GONE
                    val vehicle = mapDocumentDataToVehicle(document)
                    log(vehicle.toString())
                    loadVehicleDataToForm(vehicle)
                } else {
                    Log.e(Constants.TAG_ERROR, Constants.FB_NO_DOCUMENT)
                }
            }.addOnFailureListener { exception ->
                Log.e(Constants.TAG_ERROR, Constants.ERROR_EXCEPTION_PREFIX, exception)
            }
        } else {
            showInfoNewVehicle()
        }
    }

    private fun loadVehicleDataToForm(vehicle: VehicleFB) {
        binding.vnAcCategory.setText(vehicle.category, false)
        binding.vnAcBrand.setText(vehicle.brand, false)
        binding.vnAcModel.setText(vehicle.model, false)
        binding.vnItPlate.setText(vehicle.plate)
        binding.vnCbAvailable.isChecked = vehicle.available
// todo configurar fecha
    }

    private fun mapDocumentDataToVehicle(document: DocumentSnapshot): VehicleFB {
        val data = document.data ?: throw IllegalArgumentException("Document data was null or empty")
        return VehicleFB(
            data["available"] as Boolean,
            data["brand"] as String,
            data["category"] as String,
            data["created"] as String,
            data["model"] as String,
            data["plate"] as String,
            data["registrationDate"] as String,
            data["spents"] as List<SpentFB>,
            data["userId"] as String,
            data["vehicleId"] as String,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllBrandsFromAPI()
        loadCategoriesInSelectable()
        viewModel.modelsList.observe(viewLifecycleOwner) { list -> loadModelsInSelectable(list) }
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message -> showSnackBar(message, requireView()) }
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
            showSnackBar("falta rellenar algún campo", requireView())
            return
        }
        val ad = AlertDialogModel(
            this.requireActivity(),
            this.requireActivity().getString(R.string.alertDialog_confirm_message),
            this.requireActivity().getString(R.string.alertDialog_editVehicle_message)
        )
        showDialogAcceptCancel(ad) { accept ->
            val fb = FirebaseService.getInstance()
            if (accept) {
                try {
                    val vehicle: VehicleFB = generateVehicle()
                    viewModel.saveVehicleAndVehiclePreview(vehicle)
                    val message = requireActivity().getString(R.string.vehicle_createVehicle_successfully)
                    saveToLog(LogType.INFO, fb.auth, OperationLog.SET_VEHICLE, message)
                    navigateToVehiclesList()
                } catch (e: Error) {
                    Log.e(Constants.TAG_ERROR, "Error in vehicle creation: ${e.message}")
                }
            } else {
                val message = requireActivity().getString(R.string.vehicle_createVehicle_error)
                showSnackBar(message, requireView())
                saveToLog(LogType.ERROR, fb.auth, OperationLog.SET_VEHICLE, message)
            }
        }
    }


/*    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveVehicleToFB(vehicle: VehicleFB): Boolean {
        val responseVehicle = fbSetVehicle(vehicle)
        return responseVehicle.isSuccessful
    }*/

    private fun navigateToVehiclesList() {
        // TODO HACER LA NAVEGACIÓN AL LISTADO DE VEHÍCULOS
        requireActivity().onBackPressed()
        log("navegar al listado de vehículos")
    }


    private fun generateVehicle(): VehicleFB {
        val fb = FirebaseService.getInstance()
        return VehicleFB(
            binding.vnCbAvailable.isChecked,
            binding.vnAcBrand.text.toString(),
            translateCategory(binding.vnAcCategory.text.toString()),
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
        val categoriesList = getCategories()
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
        loadModelsInSelectable(emptyList())
    }

    private fun clearBrands() {
        val brandsSelectable = binding.vnAcBrand
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
        val modelSelectable = binding.vnAcModel
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

    private fun showInfoNewVehicle() {
        val ad = AlertDialogModel(
            this.requireActivity(),
            this.requireActivity().getString(R.string.alertDialog_show_info_newVehicle_title),
            this.requireActivity().getString(R.string.alertDialog_show_info_newVehicle_message)
        )
        showDialogAccept(ad) { }
    }
}