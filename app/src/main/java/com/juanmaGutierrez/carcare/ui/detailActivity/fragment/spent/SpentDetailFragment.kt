package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.spent

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentSpentDetailBinding
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.Spent
import com.juanmaGutierrez.carcare.model.localData.UIUserMessages
import com.juanmaGutierrez.carcare.service.generateId
import com.juanmaGutierrez.carcare.service.getTimestamp
import com.juanmaGutierrez.carcare.service.loadDataInSelectable
import com.juanmaGutierrez.carcare.service.moneyInputFormat
import com.juanmaGutierrez.carcare.service.showDatePickerDialog
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.service.toCapitalizeString
import com.juanmaGutierrez.carcare.service.transformDateIsoToString

class SpentDetailFragment : Fragment() {
    private lateinit var binding: FragmentSpentDetailBinding
    private lateinit var viewModel: SpentDetailViewModel
    private lateinit var spent: Spent
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
        this.spent = Spent(
            0.0, getTimestamp(), getTimestamp(), "", "", "", generateId()
        )
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

    private fun configureObservers() {
        configureSelectableObservers()
        configureIsLoadingObserver()
        configureSpentObserver()
        configureEditSpentObserver()
    }

    private fun configureSelectableObservers() {
        viewModel.providersSelectableList.observe(viewLifecycleOwner) { providers ->
            loadDataInSelectable(binding.sdAcProvider, providers, requireActivity())
        }
    }

    private fun getSpentFromID(): String {
        itemId = arguments?.getString("itemId") ?: ""
        if (itemId != "") {
            val vehicleId = arguments?.getString("vehicleId") ?: ""
            viewModel.getSpentFromFB(itemId, vehicleId)
            return "edit"
        }
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
        showSnackBar("pulsado botón aceptar", requireView()) {}
        // Añadir lógica de grabación del gasto
    }

    private fun buttonCancelPressed() {
        if (isAdded) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun buttonDeletePressed() {
        showSnackBar("pulsado botón borrar", requireView()) {}
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
        viewModel.spent.observe(viewLifecycleOwner) { spent ->
            loadSpentDataInForm(spent)
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
            showSnackBar(uiUM.snackbarMessages.createOrEditSuccessful, requireView()) {}
        }
    }
}