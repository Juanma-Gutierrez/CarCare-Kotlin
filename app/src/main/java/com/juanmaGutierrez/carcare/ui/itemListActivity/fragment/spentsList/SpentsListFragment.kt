package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.db.williamchart.view.AxisChartView
import com.google.android.material.carousel.CarouselLayoutManager
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.OnVehicleClickListener
import com.juanmaGutierrez.carcare.adapter.SpentAdapter
import com.juanmaGutierrez.carcare.adapter.VehicleInSpentsListAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentSpentsListBinding
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.getTimestamp
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.service.transformDateIsoToString
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

/**
 * Fragment for displaying the list of spents.
 */
class SpentsListFragment : Fragment(), OnVehicleClickListener {
    private lateinit var viewModel: SpentsListViewModel
    private lateinit var binding: FragmentSpentsListBinding
    private lateinit var vehiclesAdapter: VehicleInSpentsListAdapter
    private lateinit var spentsAdapter: SpentAdapter
    private lateinit var chartView: AxisChartView

    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SpentsListViewModel::class.java]
        binding = FragmentSpentsListBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Called when the fragment's view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getVehiclesFromFB()
        checkIfVehicleSelected()
        checkIfAnyProviderCreated()
        configureUI()
        configureObservers()
    }

    /**
     * Checks if any provider has been created.
     */
    private fun checkIfAnyProviderCreated() {
        viewModel.getProviderCount()
    }

    /**
     * Called when the fragment is resumed.
     */
    override fun onResume() {
        super.onResume()
        viewModel.setIsLoading(false)
    }

    /**
     * Checks if a vehicle has been selected from the arguments.
     */
    private fun checkIfVehicleSelected() {
        val vehicleId = arguments?.getString("vehicleId")
        vehicleId?.let { viewModel.vehicleSelectedById(it) }
    }

    /**
     * Handles vehicle click events.
     */
    override fun onVehicleClick(vehicle: VehiclePreview) {
        if (viewModel.providerCount.value!! > 0) {
            binding.slFabAddSpent.visibility = View.VISIBLE
        }
        viewModel.vehicleClicked(vehicle)
    }

    /**
     * Fetches the list of vehicles from Firebase.
     */
    private fun getVehiclesFromFB() {
        viewModel.getVehiclesListFromFB()
    }

    /**
     * Configures the UI components.
     */
    private fun configureUI() {
        configureShareButtonGone()
        configureFabButton()
    }

    /**
     * Hides the share button.
     */
    private fun configureShareButtonGone() {
        binding.slIvShareButton.visibility = View.GONE
        binding.slIvShareButton.setOnClickListener { shareSpents() }
    }

    /**
     * Shares the list of spents.
     */
    private fun shareSpents() {
        val spentsList = viewModel.spentsList.value
        if (spentsList != null) {
            var numSpents = 0
            var totalSpents = 0.0
            var textToShare = getExportHeader()
            for (spent in spentsList) {
                numSpents += 1
                totalSpents += spent.amount
                textToShare += "${spent.toExport()}\n"
            }
            textToShare += getExportFooter(numSpents, totalSpents)
            shareText(textToShare)
        }
    }

    /**
     * Shares the provided text using an intent.
     */
    private fun shareText(textToShare: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    /**
     * Configures the observers for the ViewModel LiveData.
     */
    private fun configureObservers() {
        configureVehicleListObserver()
        configureSelectedVehicleTitleObserver()
        configureNumSpentsObserver()
        configureTotalSpentsObserver()
        configureSpentsListObserver()
        configureNumSpentsHeightLayoutObserver()
        configureIsLoadingObserver()
        configureProviderCountObserver()
    }

    /**
     * Configures the observer for the provider count.
     * Updates the visibility of the add spent FAB and shows a snackbar if no providers are available.
     */
    private fun configureProviderCountObserver() {
        viewModel.providerCount.observe(viewLifecycleOwner) { count ->
            if (count == 0) {
                binding.slFabAddSpent.visibility = View.GONE
                showSnackBar(getString(R.string.providersList_noProviders), requireView()) {}
            }
        }
    }

    /**
     * Configures the observer for the vehicle list.
     * Loads the vehicles into the RecyclerView and updates the visibility of the vehicle and spent containers.
     * Shows a snackbar if no vehicles are available.
     */
    private fun configureVehicleListObserver() {
        viewModel.vehicles.observe(viewLifecycleOwner) { vehicles ->
            loadVehiclesInRV(vehicles)
            if (vehicles.isEmpty()) {
                binding.slRvVehiclesInSpentList.visibility = View.GONE
                binding.slClSpentsContainer.visibility = View.GONE
                showSnackBar(getString(R.string.vehiclesList_noVehicles), requireView()) {}
            } else {
                binding.slRvVehiclesInSpentList.visibility = View.VISIBLE
                binding.slClSpentsContainer.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Configures the observer for the selected vehicle title.
     * Updates the text and visibility of the vehicle selected TextView and share button.
     */
    private fun configureSelectedVehicleTitleObserver() {
        viewModel.selectedVehicle.observe(viewLifecycleOwner) { vehicle ->
            binding.slTvVehicleSelected.text = String.format("%1s, %2s", vehicle.brand, vehicle.model)
            binding.slIvShareButton.visibility = View.VISIBLE
        }
    }

    /**
     * Configures the observer for the number of spents.
     * Updates the text of the number of spents TextView.
     */
    private fun configureNumSpentsObserver() {
        viewModel.numSpents.observe(viewLifecycleOwner) { numSpents ->
            binding.slTvNumSpents.text = getString(R.string.spents_numSpents, numSpents)
        }
    }

    /**
     * Configures the observer for the total spents.
     * Updates the text of the total spents TextView.
     */
    private fun configureTotalSpentsObserver() {
        viewModel.totalSpents.observe(viewLifecycleOwner) { totalSpents ->
            binding.slTvTotalSpents.text = getString(R.string.spents_totalSpents, totalSpents)
        }
    }

    /**
     * Configures the observer for the spents list.
     * Loads the spents into the RecyclerView and the chart.
     */
    private fun configureSpentsListObserver() {
        viewModel.spentsList.observe(viewLifecycleOwner) { spents ->
            loadSpentsInRV(spents)
            loadSpentsInChart(spents)
        }
    }

    /**
     * Configures the observer for the loading state.
     * Updates the visibility of the loading animation.
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
     * Configures the observer for the number of spents height layout.
     * Adjusts the height of the chart based on the number of bars.
     */
    private fun configureNumSpentsHeightLayoutObserver() {
        viewModel.numSpentsHeightLayout.observe(viewLifecycleOwner) { numBars ->
            val layoutParams = chartView.layoutParams
            val scale = resources.displayMetrics.density
            val barHeight = 20
            val paddingsAndXAxisLabel = 40
            val height = (numBars * barHeight) + paddingsAndXAxisLabel
            val chartHeightPx = height * scale
            layoutParams.height = chartHeightPx.toInt()
        }
    }

    /**
     * Loads the spents into the RecyclerView.
     *
     * @param spentsList The list of spents to load.
     */
    private fun loadSpentsInRV(spentsList: List<SpentFB>) {
        spentsAdapter = SpentAdapter(spentsList, requireContext(), viewModel.selectedVehicle.value!!.vehicleId)
        binding.slRvSpents.layoutManager = LinearLayoutManager(requireContext())
        binding.slRvSpents.adapter = spentsAdapter
    }

    /**
     * Loads the vehicles into the RecyclerView.
     *
     * @param vehicles The list of vehicles to load.
     */
    private fun loadVehiclesInRV(vehicles: List<VehiclePreview>) {
        vehiclesAdapter = VehicleInSpentsListAdapter(vehicles, requireContext())
        vehiclesAdapter.setOnVehicleClickListener(this)
        binding.slRvVehiclesInSpentList.setLayoutManager(CarouselLayoutManager())
        binding.slRvVehiclesInSpentList.adapter = vehiclesAdapter
    }

    /**
     * Configures the Floating Action Button (FAB).
     * Sets its visibility and click listener to navigate to the detail activity for adding a new spent.
     */
    private fun configureFabButton() {
        binding.slFabAddSpent.visibility = View.GONE
        binding.slFabAddSpent.setOnClickListener {
            val ts = ToolbarService.getInstance()
            ts.detailTitle = getString(R.string.new_spent)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("fragmentType", "newSpent")
            intent.putExtra("vehicleId", viewModel.selectedVehicle.value!!.vehicleId)
            startActivity(intent)
        }
    }

    /**
     * Loads the spents into the chart.
     *
     * @param spents The list of spents to load into the chart.
     */
    private fun loadSpentsInChart(spents: List<SpentFB>) {
        if (spents.size > 1) {
            binding.slBcSpentsChart.visibility = View.VISIBLE
            val chartSize = viewModel.getChartSize(requireContext())
            if (chartSize > 1) {
                chartView = binding.slBcSpentsChart
                chartView.labelsFormatter = { value -> value.toInt().toString() }
                chartView.show(viewModel.generateChart(spents, chartSize))
                chartView.animate(viewModel.generateChart(spents, chartSize))
            }
        } else {
            binding.slBcSpentsChart.visibility = View.GONE
        }
    }

    /**
     * Generates the export footer.
     *
     * @param numSpents The number of spents.
     * @param totalSpents The total amount of spents.
     * @return The formatted footer string.
     */
    private fun getExportFooter(numSpents: Int, totalSpents: Double): String {
        var footer = generateHorizontalDivider()
        val textNumSpents = getString(R.string.spents_numSpents, numSpents)
        val textTotalSpents = getString(R.string.spents_totalSpents, totalSpents)
        footer += "$textNumSpents\n$textTotalSpents\n"
        return footer
    }

    /**
     * Generates the export header.
     *
     * @return The formatted header string.
     */
    private fun getExportHeader(): String {
        val date = getTimestamp().transformDateIsoToString()
        val selectedVehicle = viewModel.selectedVehicle.value
        var header = "$date ${selectedVehicle!!.brand} ${selectedVehicle.model}\n"
        header += generateHorizontalDivider()
        return header
    }

    /**
     * Generates a horizontal divider.
     *
     * @return A string consisting of a horizontal divider.
     */
    private fun generateHorizontalDivider(): String {
        val numScripts = 40
        return "-".repeat(numScripts) + "\n"
    }
}
