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
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentVehicleDetailBinding
import com.juanmaGutierrez.carcare.localData.getVehicleCategories
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.firebase.VehicleImagePackToFB
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.model.localData.UIUserMessages
import com.juanmaGutierrez.carcare.model.localData.VehicleBrandsService
import com.juanmaGutierrez.carcare.service.CameraService
import com.juanmaGutierrez.carcare.service.fbSaveImage
import com.juanmaGutierrez.carcare.service.generateId
import com.juanmaGutierrez.carcare.service.getTimestamp
import com.juanmaGutierrez.carcare.service.getVehicleCategoryTranslation
import com.juanmaGutierrez.carcare.service.loadDataInSelectable
import com.juanmaGutierrez.carcare.service.showDatePickerDialog
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.service.transformDateIsoToString
import com.juanmaGutierrez.carcare.service.transformStringToDateIso
import com.juanmaGutierrez.carcare.service.translateVehicleCategory

/**
 * Fragment responsible for handling the details of a vehicle, including creation and editing.
 */
class VehicleDetailFragment : Fragment() {
    private lateinit var binding: FragmentVehicleDetailBinding
    private lateinit var viewModel: VehicleDetailViewModel
    private val cameraService = CameraService()
    private var alertDialog: AlertDialog? = null
    private var imageURL: String? = null
    private var uiUM: UIUserMessages = UIUserMessages()

    /**
     * Called when the fragment is starting. This is where most initialization should be performed:
     * creating the fragment, initializing UI, and restoring state if needed.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[VehicleDetailViewModel::class.java]
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVehicleDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned,
     * but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureUIMessages()
        configureImageButton()
        configureDeleteImageButton()
        configureCategorySelectable()
        configureSelectables()
        configureUI()
        configureCancelButton()
        configureEditVehicleSuccessful()
        checkNewOrEdit()
    }

    /**
     * Configures the UI messages for snackbar and alert dialog.
     */
    private fun configureUIMessages() {
        uiUM.snackbarMessages.deleteSuccessful = getString(R.string.vehicle_deleteVehicle_successfully)
        uiUM.snackbarMessages.deletionError = getString(R.string.vehicle_deleteVehicle_error)
    }

    /**
     * Checks if the fragment is in 'new' or 'edit' mode and configures the UI accordingly.
     */
    private fun checkNewOrEdit() {
        when (getVehicleFromID()) {
            "new" -> {
                viewModel.setCategories(getVehicleCategories(requireActivity()))
                binding.veAcBrand.isEnabled = false
                binding.veAcModel.isEnabled = false
                val date = getTimestamp().transformDateIsoToString()
                binding.veCbDate.text = date
                binding.veBtDelete.visibility = View.GONE
                configureDateButton(date)
                requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
                resetVehicleImage()
                val vehicle = getDataFromForm(generateNewEmptyVehicle())
                configureAcceptButton(vehicle)
                configureMessage("new")
            }

            "edit" -> {
                getVehicleFromID()
                configurePreviewImage()
                configureVehicle()
                configureMessage("edit")
            }
        }
    }

    /**
     * Configures the message depending on the mode ('new' or 'edit').
     *
     * @param mode The mode of the fragment.
     */
    private fun configureMessage(mode: String) {
        when (mode) {
            "new" -> {
                uiUM.alertDialog.title = getString(R.string.alertDialog_newVehicle_title)
                uiUM.alertDialog.message = getString(R.string.alertDialog_newVehicle_message)
                uiUM.snackbarMessages.createOrEditSuccessful = getString(R.string.vehicle_createVehicle_successfully)
                uiUM.snackbarMessages.createOrEditError = getString(R.string.vehicle_createVehicle_error)
                uiUM.logMessages.createOrEditionSuccess = Constants.LOG_VEHICLE_CREATION_SUCCESSFULLY
                uiUM.logMessages.createOrEditionError = Constants.LOG_VEHICLE_CREATION_ERROR
            }

            "edit" -> {
                uiUM.alertDialog.title = getString(R.string.alertDialog_editVehicle_title)
                uiUM.alertDialog.message = getString(R.string.alertDialog_editVehicle_message)
                uiUM.snackbarMessages.createOrEditSuccessful = getString(R.string.vehicle_editVehicle_successfully)
                uiUM.snackbarMessages.createOrEditError = getString(R.string.vehicle_editVehicle_error)
                uiUM.logMessages.createOrEditionSuccess = Constants.LOG_VEHICLE_EDITION_SUCCESSFULLY
                uiUM.logMessages.createOrEditionError = Constants.LOG_VEHICLE_EDITION_ERROR
            }
        }
    }

    /**
     * Generates a new empty vehicle object.
     *
     * @return The new empty vehicle.
     */
    private fun generateNewEmptyVehicle(): VehicleFB {
        return VehicleFB(
            true,
            "",
            "",
            getTimestamp(),
            null,
            "",
            "",
            getTimestamp().transformDateIsoToString(),
            emptyList(),
            FirebaseAuth.getInstance().currentUser!!.uid,
            generateId()
        )
    }

    /**
     * Determines if the fragment is in 'new' or 'edit' mode based on the provided item ID.
     * If the item ID is not empty, fetches the vehicle details from Firebase and returns 'edit'.
     * Otherwise, returns 'new'.
     *
     * @return The mode of the fragment ('new' or 'edit').
     */
    private fun getVehicleFromID(): String {
        val itemId = arguments?.getString("itemId") ?: ""
        if (itemId != "") {
            viewModel.getVehicleFromFB(itemId)
            return "edit"
        }
        return "new"
    }

    /**
     * Configures the placeholder image for the vehicle preview.
     */
    private fun configurePreviewImage() {
        val resourceId = R.drawable.placeholder_vehicle
        val drawable = ContextCompat.getDrawable(requireContext(), resourceId)
        binding.veIvVehicleImage.setImageDrawable(drawable)
    }

    /**
     * Configures the image button to open the camera/gallery dialog.
     */
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

    /**
     * Configures the delete image button to reset the vehicle image.
     */
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
                        CameraService.image_uri = null
                        imageURL = null
                        resetVehicleImage()
                    } catch (e: Exception) {
                        Log.e(Constants.TAG, Constants.ERROR_DATABASE, e)
                    }
                }
            }
        }
    }

    /**
     * Resets the vehicle image to the placeholder image.
     */
    private fun resetVehicleImage() {
        binding.veIvVehicleImage.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(), R.drawable.placeholder_vehicle
            )
        )
    }

    /**
     * Observes changes in the vehicle data and updates the UI accordingly.
     */
    private fun configureVehicle() {
        viewModel.vehicle.observe(viewLifecycleOwner) { vehicle ->
            imageURL = vehicle.imageURL
            loadVehicleDataToForm(vehicle)
            loadVehicleImageToForm()
            viewModel.setCategories(getVehicleCategories(requireActivity()))
            viewModel.selectedCategory = binding.veAcCategory.text.toString().translateVehicleCategory()
            viewModel.getBrandsFromAPI(vehicle.category)
            viewModel.getModelsFromBrandAPI(vehicle.brand)
            configureDateButton(vehicle.registrationDate.transformDateIsoToString(Constants.DATE_FORMAT_LOCAL))
            configureAcceptButton(vehicle)
            configureDeleteButton(vehicle)
        }
    }

    /**
     * Configures the UI components related to selectable items.
     */
    private fun configureSelectables() {
        configureSelectablesObservers()
        configureSelectablesActions()
    }

    /**
     * Configures the UI components and actions related to user interactions.
     */
    private fun configureUI() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
            }
        }
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message -> showSnackBar(message, requireView()) {} }
    }

    /**
     * Configures the cancel button to close the fragment and restart the activity.
     */
    private fun configureCancelButton() {
        binding.veBtCancel.setOnClickListener {
            closeFragmentAndRestart()
        }
    }

    /**
     * Observes changes in the success state of editing the vehicle and shows a snackbar message accordingly.
     */
    private fun configureEditVehicleSuccessful() {
        viewModel.editVehicleSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                showSnackBar(uiUM.snackbarMessages.createOrEditSuccessful, requireView()) {
                    closeFragmentAndRestart()
                }
            }
        }
    }

    /**
     * Checks camera permissions. If granted, starts the camera. Otherwise, requests necessary permissions.
     */
    private fun checkCameraPermissions() {
        if (cameraService.allPermissionGranted(requireActivity())) {
            cameraService.startCamera(requireActivity(), cameraARL)
        } else {
            requestPermissions(CameraService.REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS)
        }
    }

    /**
     * Handles the result of the permission request. If permissions are granted, starts the camera.
     * If permissions are denied, shows a Snackbar message.
     */
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

    /**
     * Launcher to get the result from the camera activity. If successful, displays the captured image in the corresponding ImageView.
     */
    var cameraARL: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            binding.veIvVehicleImage.setImageURI(CameraService.image_uri)
        }
    }

    /**
     * Launcher to request permissions to access the gallery. If permissions are granted, opens the gallery. If not, shows a Snackbar message.
     */
    private val requestGalleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.READ_MEDIA_IMAGES] == true) {
                openGallery()
            } else {
                showSnackBar(getString(R.string.snackBar_noPermissions), requireView()) {}
            }
        }

    /**
     * Launcher to get the result from the gallery image selection activity.
     * If successful, displays the selected image in the corresponding ImageView.
     */
    private val pickGalleryImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { _imageurl ->
                    CameraService.image_uri = _imageurl
                    binding.veIvVehicleImage.setImageURI(CameraService.image_uri)
                }
            }
        }

    /**
     * Checks permissions and, if granted, opens the gallery. If not, requests permissions to access the gallery.
     */
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

    /**
     * Opens the gallery to select an image.
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickGalleryImageLauncher.launch(intent)
    }

    /**
     * Loads vehicle data into the form fields.
     * @param vehicle The vehicle data to be loaded.
     */
    private fun loadVehicleDataToForm(vehicle: VehicleFB) {
        val category = vehicle.category.getVehicleCategoryTranslation(requireContext())
        binding.veAcCategory.setText(category, false)
        binding.veAcBrand.setText(vehicle.brand, false)
        binding.veAcModel.setText(vehicle.model, false)
        binding.veItPlate.setText(vehicle.plate)
        binding.veCbAvailable.isChecked = vehicle.available
        binding.veCbDate.text = vehicle.registrationDate.transformDateIsoToString()
    }

    /**
     * Observes the vehicle image and loads it into the ImageView.
     */
    private fun loadVehicleImageToForm() {
        viewModel.vehicleImage.observe(viewLifecycleOwner) { url ->
            binding.veIvVehicleImage.load(url)
        }
    }

    /**
     * Configures the date button to show a date picker dialog when clicked.
     * @param date The initial date to be displayed in the dialog.
     */
    private fun configureDateButton(date: String) {
        binding.veCbDate.setOnClickListener {
            showDatePickerDialog(
                date, requireActivity().getString(R.string.vehicle_editVehicle_calendarTitle), childFragmentManager
            ) { selectedDate ->
                binding.veCbDate.text = selectedDate
            }
        }
    }

    /**
     * Configures observers for selectable fields (category, brand, and model).
     */
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

    /**
     * Configures actions related to selectable fields.
     */
    private fun configureSelectablesActions() {
        configureCategorySelectable()
        configureBrandSelectable()
    }

    /**
     * Configures the category selectable field.
     */
    private fun configureCategorySelectable() {
        val categorySelectable = binding.veAcCategory
        val categoriesList = getVehicleCategories(requireActivity())
        categorySelectable.setOnItemClickListener { _, _, _, id ->
            binding.veAcBrand.isEnabled = true
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
        viewModel.getBrandsFromAPI(viewModel.selectedCategory)
        binding.veAcBrand.isEnabled = true
    }

    /**
     * Clears the brand selectable field.
     */
    private fun clearBrandSelectable() {
        binding.veAcBrand.setText("")
        binding.veAcModel.isEnabled = false
        loadDataInSelectable(binding.veAcModel, emptyList(), requireActivity())
    }

    /**
     * Clears the model selectable field.
     */
    private fun clearModelSelectable() {
        binding.veAcModel.setText("")
    }

    /**
     * Configures the brand selectable field.
     */
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

    /**
     * Configures the accept button. Validates all fields before accepting.
     * @param vehicle The vehicle data to be accepted.
     */
    private fun configureAcceptButton(vehicle: VehicleFB) {
        binding.veBtAccept.setOnClickListener {
            if (allFieldsValid()) {
                acceptVehicle(vehicle)
            } else {
                showSnackBar(getString(R.string.item_vehicle_not_all_fields), requireView()) {}
            }
        }
    }

    /**
     * Validates all form fields.
     * @return true if all fields are valid, false otherwise.
     */
    private fun allFieldsValid(): Boolean {
        if (binding.veAcCategory.text.isNullOrEmpty()) return false
        if (binding.veAcBrand.text.isNullOrEmpty()) return false
        if (binding.veAcModel.text.isNullOrEmpty()) return false
        if (binding.veItPlate.text.isNullOrEmpty()) return false
        return true
    }

    /**
     * Configures the delete button for the vehicle.
     * @param vehicle The vehicle to be deleted.
     */
    private fun configureDeleteButton(vehicle: VehicleFB) {
        binding.veBtDelete.setOnClickListener {
            deleteVehicle(vehicle)
        }
    }

    /**
     * Handles accepting the changes made to the vehicle.
     * @param vehicle The vehicle to be accepted.
     */
    private fun acceptVehicle(vehicle: VehicleFB) {
        val ad = AlertDialogModel(
            this.requireActivity(),
            uiUM.alertDialog.title,
            uiUM.alertDialog.message,
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

    /**
     * Accepts the changes made to the vehicle and saves them to Firebase.
     * @param vehicle The vehicle to be edited and saved.
     */
    private fun acceptEditVehicle(vehicle: VehicleFB) {
        val name = if (vehicle.vehicleId.isNotEmpty()) vehicle.vehicleId else generateId()
        saveVehicleImageToFB(name, vehicle)
        saveVehicleToFB(vehicle)
    }

    /**
     * Saves the vehicle image to Firebase storage.
     * @param name The name of the image.
     * @param vehicle The vehicle associated with the image.
     */
    private fun saveVehicleImageToFB(name: String, vehicle: VehicleFB) {
        val uri = CameraService.image_uri
        if (uri != null) {
            val imagePack = VehicleImagePackToFB(
                requireContext(), uri, name, vehicle
            )
            imageURL = fbSaveImage(imagePack)
        }
    }

    /**
     * Saves the vehicle data to Firebase Firestore.
     * @param vehicle The vehicle data to be saved.
     */
    private fun saveVehicleToFB(vehicle: VehicleFB) {
        val editedVehicle: VehicleFB = getDataFromForm(vehicle)
        viewModel.editVehicle(editedVehicle)
        viewModel.editVehicleSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                showSnackBar(uiUM.snackbarMessages.createOrEditSuccessful, requireView()) {
                    closeFragmentAndRestart()
                }
            }
        }
    }

    /**
     * Retrieves vehicle data from the form fields.
     * @param vehicle The original vehicle data.
     * @return The updated vehicle data.
     */
    private fun getDataFromForm(vehicle: VehicleFB): VehicleFB {
        return VehicleFB(
            binding.veCbAvailable.isChecked,
            binding.veAcBrand.text.toString(),
            binding.veAcCategory.text.toString().translateVehicleCategory(),
            vehicle.created,
            imageURL,
            binding.veAcModel.text.toString(),
            binding.veItPlate.text.toString(),
            binding.veCbDate.text.toString().transformStringToDateIso(),
            vehicle.spents,
            vehicle.userId,
            vehicle.vehicleId
        )
    }

    /**
     * Deletes the vehicle from Firebase.
     * @param vehicle The vehicle to be deleted.
     */
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

    /**
     * Accepts the deletion of the vehicle.
     * @param vehicle The vehicle to be deleted.
     */
    private fun acceptDeleteVehicle(vehicle: VehicleFB) {
        viewModel.deleteVehicle(vehicle)
    }

    /**
     * Closes the fragment and restarts the activity.
     */
    private fun closeFragmentAndRestart() {
        if (isAdded) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}
