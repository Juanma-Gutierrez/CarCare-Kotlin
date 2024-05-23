package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.spent

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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

class SpentDetailFragment : Fragment() {
    private lateinit var binding: FragmentSpentDetailBinding
    private lateinit var viewModel: SpentDetailViewModel
    private lateinit var vehicleToSave: VehicleFB
    private var itemId = ""
    private var fragmentType = "new"
    private var uiUM: UIUserMessages = UIUserMessages()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpentDetailBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[SpentDetailViewModel::class.java]
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createEmptySpent() {
        val spent = Spent(
            0.0, getTimestamp(), getTimestamp(), "", "", "", generateId()
        )
        viewModel.setSpent(spent)
    }

    private fun configureUIMessages() {
        uiUM.snackbarMessages.deleteSuccessful = getString(R.string.spent_deleteSpent_successfully)
        uiUM.snackbarMessages.deletionError = getString(R.string.spent_deleteSpent_error)
        uiUM.logMessages.deleteSuccess = Constants.LOG_SPENT_DELETION_SUCCESSFULLY
        uiUM.logMessages.deleteError = Constants.LOG_SPENT_DELETION_ERROR
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkNewOrCreate() {
        fragmentType = getSpentFromID()
        when (fragmentType) {
            "new" -> configureNewSpentUI()
            "edit" -> configureEditSpentUI()
        }
        viewModel.uiUM = uiUM
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureUI() {
        binding.sdBtAccept.setOnClickListener { buttonAcceptPressed() }
        binding.sdBtCancel.setOnClickListener { buttonCancelPressed() }
        binding.sdBtDelete.setOnClickListener { buttonDeletePressed() }
        binding.sdBtDate.setOnClickListener { buttonSpentDateClicked() }
    }

    private fun configureSelectables() {
        viewModel.getProviders()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureObservers() {
        configureSelectedVehicleObserver()
        configureSelectableObservers()
        configureIsLoadingObserver()
        configureSpentObserver()
        configureEditSpentObserver()
    }

    private fun configureSelectedVehicleObserver() {
        viewModel.selectedVehicle.observe(viewLifecycleOwner) { vehicle -> vehicleToSave = vehicle }
    }

    private fun configureSelectableObservers() {
        viewModel.providersSelectableList.observe(viewLifecycleOwner) { providers ->
            loadDataInSelectable(binding.sdAcProvider, providers, requireActivity())
        }
    }

    private fun getSpentFromID(): String {
        itemId = getItemId()
        val vehicleId = getVehicleId()
        viewModel.getSpentFromFB(itemId, vehicleId)
        if (itemId != "") return "edit"
        return "new"
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    private fun configureEditSpentUI() {
        binding.sdBtDelete.visibility = View.VISIBLE
        uiUM.alertDialog.title = getString(R.string.alertDialog_editSpent_title)
        uiUM.alertDialog.message = getString(R.string.alertDialog_editSpent_message)
        uiUM.snackbarMessages.createOrEditSuccessful = getString(R.string.spent_editSpent_successfully)
        uiUM.snackbarMessages.createOrEditError = getString(R.string.spent_editSpent_error)
        uiUM.logMessages.createOrEditionSuccess = Constants.LOG_SPENT_EDITION_SUCCESSFULLY
        uiUM.logMessages.createOrEditionError = Constants.LOG_SPENT_EDITION_ERROR
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun acceptButtonClicked() {
        viewModel.setSpent(getSpentFromForm())
        vehicleToSave = viewModel.selectedVehicle.value!!
        when (fragmentType) {
            "new" -> addNewSpent()
            "edit" -> editSpent()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addNewSpent() {
        val spentsList = getSpentsListFromVehicleSelected()
        spentsList.add(formatSpent(viewModel.spent.value!!))
        spentsList.sortByDescending { it.date }
        val vehicleUpdated = updateVehicleWithSpents(spentsList)
        viewModel.saveVehicleToFB(vehicleUpdated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        viewModel.saveVehicleToFB(vehicleUpdated)
    }


    private fun getSpentsListFromVehicleSelected(): MutableList<Spent> {
        val spentsFBListHashMap = mapSpentListFBToSpentList(vehicleToSave.spents as List<Map<String, Any>>)
        return spentsFBListHashMap.map { mapSpentFBToSpent(it) }.toMutableList()
    }

    private fun getSpentFromForm(): Spent {
        return Spent(
            binding.sdTvAmount.text.toString().toDouble(),
            viewModel.spent.value!!.created,
            binding.sdBtDate.text.toString(),
            binding.sdTvObservations.text.toString(),
            viewModel.spent.value!!.providerId,
            binding.sdAcProvider.text.toString(),
            viewModel.spent.value!!.spentId,
        )
    }

    private fun checkAllFieldsValid(): Boolean {
        var valid = true
        if (binding.sdAcProvider.text.isNullOrEmpty()) valid = false
        if (binding.sdTvAmount.text.isNullOrEmpty()) valid = false
        if (!valid) showSnackBar(getString(R.string.snackBar_fieldsEmpty), requireView()) {}
        return valid
    }

    private fun buttonCancelPressed() {
        if (isAdded) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

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

    private fun deleteButtonClicked() {
        val itemId = getItemId()
        val vehicleId = getVehicleId()
        viewModel.deleteSpent(itemId, vehicleId)
    }


    private fun getItemId(): String {
        return arguments?.getString("itemId") ?: ""
    }

    private fun getVehicleId(): String {
        return arguments?.getString("vehicleId") ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buttonSpentDateClicked() {
        val date = binding.sdBtDate.text.toString()
        showDatePickerDialog(
            date, requireActivity().getString(R.string.spent_editSpent_calendarTitle), childFragmentManager
        ) { selectedDate -> binding.sdBtDate.text = selectedDate }
    }

    private fun configureIsLoadingObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureSpentObserver() {
        viewModel.spent.observe(viewLifecycleOwner) { newSpent ->
            loadSpentDataInForm(newSpent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadSpentDataInForm(spent: Spent) {
        binding.sdAcProvider.setText(spent.providerName, false)
        binding.sdTvAmount.setText(spent.amount.moneyInputFormat())
        binding.sdTvObservations.setText(spent.observations.toCapitalizeString())
        if (binding.sdBtDate.text.isEmpty()) binding.sdBtDate.text = spent.date.transformDateIsoToString()
    }

    private fun configureEditSpentObserver() {
        viewModel.editSpentSuccessful.observe(viewLifecycleOwner) {
            showSnackBar(uiUM.snackbarMessages.createOrEditSuccessful, requireView()) { closeFragmentAndRestart() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatSpent(spent: Spent): Spent {
        var formattedSpent = spent
        formattedSpent.date = spent.date.transformStringToDateIso()
        return formattedSpent
    }

    private fun updateVehicleWithSpents(spentsList: MutableList<Spent>): VehicleFB {
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