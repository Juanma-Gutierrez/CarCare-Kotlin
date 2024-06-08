package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.spent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentSpentDetailBinding
import com.juanmaGutierrez.carcare.mapping.mapSpentFBToSpent
import com.juanmaGutierrez.carcare.mapping.mapSpentListFBToSpentList
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.model.firebase.VehicleFB
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.model.localData.Spent
import com.juanmaGutierrez.carcare.model.localData.UIUserMessages
import com.juanmaGutierrez.carcare.service.generateId
import com.juanmaGutierrez.carcare.service.getTimestamp
import com.juanmaGutierrez.carcare.service.loadDataInSelectable
import com.juanmaGutierrez.carcare.service.moneyInputFormat
import com.juanmaGutierrez.carcare.service.showDatePickerDialog
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.service.toCapitalizeString
import com.juanmaGutierrez.carcare.service.transformDateIsoToString
import com.juanmaGutierrez.carcare.service.transformStringToDateIso
import com.juanmaGutierrez.carcare.ui.itemListActivity.ItemListActivity

/**
 * Fragment for displaying and managing details of a spent item.
 */
class SpentDetailFragment : Fragment() {
    private lateinit var binding: FragmentSpentDetailBinding
    private lateinit var viewModel: SpentDetailViewModel
    private lateinit var vehicleToSave: VehicleFB
    private lateinit var providerNamesList: List<String>
    private lateinit var providerIdList: List<String>
    private var itemId = ""
    private var fragmentType = "new"
    private var uiUM: UIUserMessages = UIUserMessages()

    /**
     * Initializes the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpentDetailBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[SpentDetailViewModel::class.java]
        return binding.root
    }

    /**
     * Configures the fragment's UI components and observes LiveData.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init()
        createEmptySpent()
        configureUIMessages()
        checkNewOrCreate()
        configureUI()
        configureSelectables()
        configureObservers()
    }

    /**
     * Creates an empty spent item.
     */
    private fun createEmptySpent() {
        val spent = Spent(
            0.0, getTimestamp(), getTimestamp(), "", "", "", generateId()
        )
        viewModel.setSpent(spent)
    }

    /**
     * Configures UI messages for alerts and snackbars.
     */
    private fun configureUIMessages() {
        uiUM.snackbarMessages.deleteSuccessful = getString(R.string.spent_deleteSpent_successfully)
        uiUM.snackbarMessages.deletionError = getString(R.string.spent_deleteSpent_error)
        uiUM.logMessages.deleteSuccess = Constants.LOG_SPENT_DELETION_SUCCESSFULLY
        uiUM.logMessages.deleteError = Constants.LOG_SPENT_DELETION_ERROR
    }

    /**
     * Checks if the fragment is for a new spent item or for editing an existing one.
     */
    private fun checkNewOrCreate() {
        fragmentType = getSpentFromID()
        when (fragmentType) {
            "new" -> configureNewSpentUI()
            "edit" -> configureEditSpentUI()
        }
        viewModel.uiUM = uiUM
    }

    /**
     * Configures click listeners for UI components.
     */
    private fun configureUI() {
        binding.sdBtDate.setOnClickListener { buttonSpentDateClicked() }
        binding.sdBtAccept.setOnClickListener { buttonAcceptPressed() }
        binding.sdBtCancel.setOnClickListener { buttonCancelPressed() }
        binding.sdBtDelete.setOnClickListener { buttonDeletePressed() }
    }

    /**
     * Configures selectables and observes providers LiveData.
     */
    private fun configureSelectables() {
        viewModel.getProviders()
    }

    /**
     * Configures observers for LiveData.
     */
    private fun configureObservers() {
        configureSelectedVehicleObserver()
        configureSelectableObservers()
        configureIsLoadingObserver()
        configureSpentObserver()
        configureEditSpentObserver()
    }

    /**
     * Configures observer for selected vehicle LiveData.
     */
    private fun configureSelectedVehicleObserver() {
        viewModel.selectedVehicle.observe(viewLifecycleOwner) { vehicle -> vehicleToSave = vehicle }
    }

    /**
     * Configures observer for selectable providers LiveData.
     */
    private fun configureSelectableObservers() {
        viewModel.providersSelectableList.observe(viewLifecycleOwner) { providers ->
            providerNamesList = providers.map { it.name }
            providerIdList = providers.map { it.providerId }
            loadDataInSelectable(binding.sdAcProvider, providerNamesList, requireActivity())
        }
    }

    /**
     * Determines if the fragment is for a new spent item or for editing an existing one.
     */
    private fun getSpentFromID(): String {
        itemId = getItemId()
        val vehicleId = getVehicleId()
        viewModel.getSpentFromFB(itemId, vehicleId)
        if (itemId != "") return "edit"
        return "new"
    }

    /**
     * Configures UI for a new spent item.
     */
    private fun configureNewSpentUI() {
        viewModel.setIsLoading(false)
        binding.sdBtDelete.visibility = View.GONE
        uiUM.alertDialog.title = getString(R.string.alertDialog_newSpent_title)
        uiUM.alertDialog.message = getString(R.string.alertDialog_newSpent_message)
        uiUM.snackbarMessages.createOrEditSuccessful = getString(R.string.spent_createSpent_successfully)
        uiUM.snackbarMessages.createOrEditError = getString(R.string.spent_createSpent_error)
        uiUM.logMessages.createOrEditionSuccess = Constants.LOG_SPENT_CREATION_SUCCESSFULLY
        uiUM.logMessages.createOrEditionError = Constants.LOG_SPENT_CREATION_ERROR
        binding.sdBtDate.text = getTimestamp().transformDateIsoToString()
    }

    /**
     * Configures UI for editing an existing spent item.
     */
    private fun configureEditSpentUI() {
        binding.sdBtDelete.visibility = View.VISIBLE
        uiUM.alertDialog.title = getString(R.string.alertDialog_editSpent_title)
        uiUM.alertDialog.message = getString(R.string.alertDialog_editSpent_message)
        uiUM.snackbarMessages.createOrEditSuccessful = getString(R.string.spent_editSpent_successfully)
        uiUM.snackbarMessages.createOrEditError = getString(R.string.spent_editSpent_error)
        uiUM.logMessages.createOrEditionSuccess = Constants.LOG_SPENT_EDITION_SUCCESSFULLY
        uiUM.logMessages.createOrEditionError = Constants.LOG_SPENT_EDITION_ERROR
    }

    /**
     * Handles the button press event for accepting changes.
     */
    private fun buttonAcceptPressed() {
        if (checkAllFieldsValid()) {
            val title = uiUM.alertDialog.title
            val message = uiUM.alertDialog.message
            val icon = AppCompatResources.getDrawable(requireActivity(), R.drawable.icon_edit)
            val ad = AlertDialogModel(this.requireActivity(), title, message, icon)
            showDialogAcceptCancel(ad) { accept ->
                if (accept) {
                    try {
                        acceptButtonClicked()
                    } catch (e: Exception) {
                        Log.e(Constants.TAG, Constants.ERROR_DATABASE, e)
                    }
                }
            }
        } else {
            showSnackBar(getString(R.string.error_emptyFields), requireView()) {}
        }
    }

    /**
     * Handles the logic after the accept button is clicked.
     */
    private fun acceptButtonClicked() {
        viewModel.setSpent(getSpentFromForm())
        vehicleToSave = viewModel.selectedVehicle.value!!
        when (fragmentType) {
            "new" -> addNewSpent()
            "edit" -> editSpent()
        }
    }

    /**
     * Adds a new spent item.
     */
    private fun addNewSpent() {
        val spentsList = getSpentsListFromVehicleSelected()
        spentsList.add(formatSpent(viewModel.spent.value!!))
        spentsList.sortByDescending { it.date }
        val vehicleUpdated = updateVehicleWithSpents(spentsList)
        viewModel.saveVehicleToFB(
            vehicleUpdated, uiUM.logMessages.createOrEditionSuccess, uiUM.logMessages.createOrEditionError
        )
    }

    /**
     * Edits an existing spent item.
     */
    private fun editSpent() {
        val spentsList = getSpentsListFromVehicleSelected()
        val updatedSpent = formatSpent(viewModel.spent.value!!)
        val index = spentsList.indexOfFirst { it.spentId == updatedSpent.spentId }
        if (index != -1) {
            spentsList[index] = updatedSpent
        } else {
            spentsList.add(updatedSpent)
        }
        spentsList.sortByDescending { it.date }
        val vehicleUpdated = updateVehicleWithSpents(spentsList)
        viewModel.saveVehicleToFB(
            vehicleUpdated, uiUM.logMessages.createOrEditionSuccess, uiUM.logMessages.createOrEditionError
        )
    }

    /**
     * Retrieves a list of spent items from the selected vehicle.
     */
    private fun getSpentsListFromVehicleSelected(): MutableList<Spent> {
        val spentsFBListHashMap = mapSpentListFBToSpentList(vehicleToSave.spents as List<Map<String, Any>>)
        return spentsFBListHashMap.map { mapSpentFBToSpent(it) }.toMutableList()
    }

    /**
     * Retrieves a spent item from the form's input.
     */
    private fun getSpentFromForm(): Spent {
        val index = providerNamesList.indexOf(binding.sdAcProvider.text.toString())
        val providerId = providerIdList[index]
        return Spent(
            viewModel.convertNumberToLocale(binding.sdTvAmount.text.toString()),
            viewModel.spent.value!!.created,
            binding.sdBtDate.text.toString(),
            binding.sdTvObservations.text.toString(),
            providerId,
            binding.sdAcProvider.text.toString(),
            viewModel.spent.value!!.spentId,
        )
    }

    /**
     * Checks if all required fields are valid.
     */
    private fun checkAllFieldsValid(): Boolean {
        var valid = true
        if (binding.sdAcProvider.text.isNullOrEmpty()) valid = false
        if (binding.sdTvAmount.text.isNullOrEmpty()) valid = false
        if (!valid) showSnackBar(getString(R.string.snackBar_fieldsEmpty), requireView()) {}
        return valid
    }

    /**
     * Handles the button press event for cancelling.
     */
    private fun buttonCancelPressed() {
        if (isAdded) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Handles the button press event for deleting a spent item.
     */
    private fun buttonDeletePressed() {
        val title = getString(R.string.alertDialog_deleteSpent_title)
        val message = getString(R.string.alertDialog_deleteSpent_message)
        val icon = AppCompatResources.getDrawable(requireActivity(), R.drawable.icon_trash)
        val ad = AlertDialogModel(this.requireActivity(), title, message, icon)
        showDialogAcceptCancel(ad) { accept ->
            if (accept) {
                try {
                    deleteButtonClicked()
                } catch (e: Exception) {
                    Log.e(Constants.TAG, Constants.ERROR_DATABASE, e)
                }
            }
        }
    }

    /**
     * Handles the button press event for deleting a spent item.
     */
    private fun deleteButtonClicked() {
        val itemId = getItemId()
        val vehicleId = getVehicleId()
        viewModel.deleteSpent(itemId, vehicleId)
    }

    /**
     * Retrieves the item ID from the fragment arguments.
     */
    private fun getItemId(): String {
        return arguments?.getString("itemId") ?: ""
    }

    /**
     * Retrieves the vehicle ID from the fragment arguments.
     */
    private fun getVehicleId(): String {
        return arguments?.getString("vehicleId") ?: ""
    }

    /**
     * Handles the button press event for selecting the spent date.
     */
    private fun buttonSpentDateClicked() {
        val date = binding.sdBtDate.text.toString()
        showDatePickerDialog(
            date, requireActivity().getString(R.string.spent_editSpent_calendarTitle), childFragmentManager
        ) { selectedDate -> binding.sdBtDate.text = selectedDate }
    }

    /**
     * Observes the isLoading LiveData and updates UI visibility accordingly.
     */
    private fun configureIsLoadingObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
            }
        }
    }

    /**
     * Observes the spent LiveData and loads the spent data into the form.
     */
    private fun configureSpentObserver() {
        viewModel.spent.observe(viewLifecycleOwner) { newSpent ->
            loadSpentDataInForm(newSpent)
        }
    }

    /**
     * Loads spent data into the form UI.
     */
    private fun loadSpentDataInForm(spent: Spent) {
        binding.sdAcProvider.setText(spent.providerName, false)
        binding.sdTvAmount.setText(spent.amount.moneyInputFormat())
        binding.sdTvObservations.setText(spent.observations.toCapitalizeString())
        if (binding.sdBtDate.text.isEmpty()) binding.sdBtDate.text = spent.date.transformDateIsoToString()
    }

    /**
     * Observes the editSpentSuccessful LiveData and shows a snackbar message upon success.
     */
    private fun configureEditSpentObserver() {
        viewModel.editSpentSuccessful.observe(viewLifecycleOwner) {
            showSnackBar(uiUM.snackbarMessages.createOrEditSuccessful, requireView()) { closeFragmentAndRestart() }
        }
    }

    /**
     * Formats the spent item's date to ISO format.
     */
    private fun formatSpent(spent: Spent): Spent {
        spent.date = spent.date.transformStringToDateIso()
        return spent
    }

    /**
     * Updates the vehicle with the provided spent list.
     */
    private fun updateVehicleWithSpents(spentsList: List<Spent>): VehicleFB {
        return VehicleFB(
            available = vehicleToSave.available,
            brand = vehicleToSave.brand,
            category = vehicleToSave.category,
            created = vehicleToSave.category,
            imageURL = vehicleToSave.imageURL,
            model = vehicleToSave.model,
            plate = vehicleToSave.plate,
            registrationDate = vehicleToSave.registrationDate,
            spents = spentsList as List<SpentFB>,
            userId = vehicleToSave.userId,
            vehicleId = vehicleToSave.vehicleId
        )
    }

    /**
     * Closes the fragment and restarts the ItemListActivity.
     */
    private fun closeFragmentAndRestart() {
        if (isAdded) {
            requireActivity().supportFragmentManager.popBackStackImmediate(
                null, FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            val intent = Intent(context, ItemListActivity::class.java).apply {
                putExtra("destinationFragment", "spentsList")
                putExtra("itemId", itemId)
                putExtra("vehicleId", vehicleToSave.vehicleId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }
}