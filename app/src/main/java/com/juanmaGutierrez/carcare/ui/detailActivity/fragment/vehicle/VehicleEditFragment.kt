package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentVehicleEditBinding
import com.juanmaGutierrez.carcare.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.localData.getCategories
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.service.CameraService
import com.juanmaGutierrez.carcare.service.getCategoryTranslation
import com.juanmaGutierrez.carcare.service.loadDataInSelectable
import com.juanmaGutierrez.carcare.service.showDatePickerDialog
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.service.transformDateIsoToString
import com.juanmaGutierrez.carcare.service.transformStringToDateIso
import com.juanmaGutierrez.carcare.service.translateCategory

class VehicleEditFragment : Fragment() {
    private lateinit var binding: FragmentVehicleEditBinding
    private lateinit var viewModel: VehicleEditViewModel
    private val cameraService = CameraService()
    private var alertDialog: AlertDialog? = null

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getVehicleFromID()
        configurePreviewImage()
        configureImageButton()
        configureDeleteImageButton()
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

    private fun configurePreviewImage() {
        val resourceId = resources.getIdentifier("placeholder_vehicle", "drawable", requireContext().packageName)
        val drawable = ContextCompat.getDrawable(requireContext(), resourceId)
        binding.veIvVehicleImage.setImageDrawable(drawable)
    }

    private fun configureImageButton() {
        binding.veIvCameraButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_camera_gallery, null)
            dialogView.findViewById<ImageView>(R.id.camera_icon).setOnClickListener {
                checkCameraPermissions()
                alertDialog?.dismiss()
            }
            dialogView.findViewById<ImageView>(R.id.gallery_icon).setOnClickListener {
                checkPermissionAndOpenGallery()
                alertDialog?.dismiss()
            }
            alertDialog = MaterialAlertDialogBuilder(requireContext()).setView(dialogView).show()
        }
    }

    private fun configureDeleteImageButton() {
        binding.veIvDeleteImageButton.setOnClickListener {
            val ad = AlertDialogModel(
                this.requireActivity(),
                this.requireActivity().getString(R.string.alertDialog_confirm_message),
                this.requireActivity().getString(R.string.alertDialog_deleteImage),
                AppCompatResources.getDrawable(requireActivity(), R.drawable.icon_trash)
            )
            showDialogAcceptCancel(ad) { accept ->
                if (accept) {
                    try {
                        val cameraService = CameraService()
                        cameraService.image_uri = null
                        binding.veIvVehicleImage.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(), R.drawable.placeholder_vehicle
                            )
                        )
                    } catch (e: Exception) {
                        Log.e(Constants.TAG, Constants.ERROR_DATABASE, e)
                    }
                }
            }
        }
    }

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

    private fun configureSelectables() {
        configureSelectablesObservers()
        configureSelectablesActions()
    }

    private fun configureUI() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
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

    /**
     * Camera settings
     */
    private fun checkCameraPermissions() {
        if (cameraService.allPermissionGranted(requireActivity())) {
            cameraService.startCamera(requireActivity(), cameraARL)
        } else {
            requestPermissions(CameraService.REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if (cameraService.allPermissionGranted(requireActivity())) {
                cameraService.startCamera(requireActivity(), cameraARL)
            } else {
                showSnackBar(getString(R.string.snackBar_noPermissions), requireView()) {}
            }
        }
    }

    var cameraARL: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            binding.veIvVehicleImage.setImageURI(cameraService.image_uri)
        }
    }

    /**
     * Gallery config
     */
    private val requestGalleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.READ_MEDIA_IMAGES] == true) {
                openGallery()
            } else {
                showSnackBar(getString(R.string.snackBar_noPermissions), requireView()) {}
            }
        }

    private val pickGalleryImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { _imageurl ->
                    cameraService.image_uri = _imageurl
                    binding.veIvVehicleImage.setImageURI(cameraService.image_uri)
                }
            }
        }

    private fun checkPermissionAndOpenGallery() {
        val permissionsToRequest = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        val permissionsNeeded = mutableListOf<String>()

        for (permission in permissionsToRequest) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            requestGalleryPermissionLauncher.launch(permissionsNeeded.toTypedArray())
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickGalleryImageLauncher.launch(intent)
    }

    private fun loadVehicleDataToForm(vehicle: VehicleFB) {
        val category = vehicle.category.getCategoryTranslation(requireContext())
        binding.veAcCategory.setText(category, false)
        binding.veAcBrand.setText(vehicle.brand, false)
        binding.veAcModel.setText(vehicle.model, false)
        binding.veItPlate.setText(vehicle.plate)
        binding.veCbAvailable.isChecked = vehicle.available
        binding.veCbDate.text = vehicle.registrationDate.transformDateIsoToString()
    }

    private fun configureDateButton(vehicle: VehicleFB) {
        binding.veCbDate.setOnClickListener {
            showDatePickerDialog(
                vehicle.registrationDate.transformDateIsoToString(Constants.DATE_FORMAT_LOCAL),
                requireActivity().getString(R.string.vehicle_editVehicle_calendarTitle),
                childFragmentManager
            ) { selectedDate ->
                binding.veCbDate.text = selectedDate
            }
        }
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

    private fun configureVehicleButtons(vehicle: VehicleFB) {
        binding.veBtAccept.setOnClickListener {
            editVehicle(vehicle)
        }
        binding.veBtDelete.setOnClickListener {
            deleteVehicle(vehicle)
        }
    }

    private fun editVehicle(vehicle: VehicleFB) {
        val ad = AlertDialogModel(
            this.requireActivity(),
            this.requireActivity().getString(R.string.alertDialog_editVehicle_title),
            this.requireActivity().getString(R.string.alertDialog_editVehicle_message),
            AppCompatResources.getDrawable(requireActivity(), R.drawable.icon_edit)
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

    private fun getDataFromForm(v: VehicleFB): VehicleFB {
        return VehicleFB(
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
    }

    private fun deleteVehicle(vehicle: VehicleFB) {
        val ad = AlertDialogModel(
            this.requireActivity(),
            this.requireActivity().getString(R.string.alertDialog_deleteVehicle_title),
            this.requireActivity().getString(R.string.alertDialog_deleteVehicle_message),
            AppCompatResources.getDrawable(requireActivity(), R.drawable.icon_trash)
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
    }

    private fun closeFragment() {
        if (isAdded) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}
