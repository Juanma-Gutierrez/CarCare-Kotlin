package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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

class SpentsListFragment : Fragment(), OnVehicleClickListener {
    private lateinit var viewModel: SpentsListViewModel
    private lateinit var binding: FragmentSpentsListBinding
    private lateinit var vehiclesAdapter: VehicleInSpentsListAdapter
    private lateinit var spentsAdapter: SpentAdapter
    private lateinit var chartView: AxisChartView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SpentsListViewModel::class.java]
        binding = FragmentSpentsListBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getVehiclesFromFB()
        checkIfVehicleSelected()
        checkIfAnyProviderCreated()
        configureUI()
        configureObservers()
    }

    private fun checkIfAnyProviderCreated() {
        viewModel.getProviderCount()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setIsLoading(false)
    }

    private fun checkIfVehicleSelected() {
        val vehicleId = arguments?.getString("vehicleId")
        vehicleId?.let { viewModel.vehicleSelectedById(it) }
    }

    override fun onVehicleClick(vehicle: VehiclePreview) {
        viewModel.vehicleClicked(vehicle)
    }

    private fun getVehiclesFromFB() {
        viewModel.getVehiclesListFromFB()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureUI() {
        configureShareButtonGone()
        configureFabButton()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureShareButtonGone() {
        binding.slIvShareButton.visibility = View.GONE
        binding.slIvShareButton.setOnClickListener { shareSpents() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    private fun shareText(textToShare: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    private fun configureProviderCountObserver() {
        viewModel.providerCount.observe(viewLifecycleOwner) { count ->
            if (count == 0) {
                binding.slFabAddSpent.visibility = View.GONE
                showSnackBar(getString(R.string.providersList_noProviders), requireView()) {}
            } else {
                binding.slFabAddSpent.visibility = View.VISIBLE
            }
        }
    }

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

    private fun configureSelectedVehicleTitleObserver() {
        viewModel.selectedVehicle.observe(viewLifecycleOwner) { vehicle ->
            binding.slTvVehicleSelected.text = String.format("%1s, %2s", vehicle.brand, vehicle.model)
            binding.slIvShareButton.visibility = View.VISIBLE
        }
    }

    private fun configureNumSpentsObserver() {
        viewModel.numSpents.observe(viewLifecycleOwner) { numSpents ->
            binding.slTvNumSpents.text = getString(R.string.spents_numSpents, numSpents)
        }
    }

    private fun configureTotalSpentsObserver() {
        viewModel.totalSpents.observe(viewLifecycleOwner) { totalSpents ->
            binding.slTvTotalSpents.text = getString(R.string.spents_totalSpents, totalSpents)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureSpentsListObserver() {
        viewModel.spentsList.observe(viewLifecycleOwner) { spents ->
            loadSpentsInRV(spents)
            loadSpentsInChart(spents)
        }
    }

    private fun configureIsLoadingObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
            }
        }
    }

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

    private fun loadSpentsInRV(spentsList: List<SpentFB>) {
        spentsAdapter = SpentAdapter(spentsList, requireContext(), viewModel.selectedVehicle.value!!.vehicleId)
        binding.slRvSpents.layoutManager = LinearLayoutManager(requireContext())
        binding.slRvSpents.adapter = spentsAdapter
    }

    private fun loadVehiclesInRV(vehicles: List<VehiclePreview>) {
        vehiclesAdapter = VehicleInSpentsListAdapter(vehicles, requireContext())
        vehiclesAdapter.setOnVehicleClickListener(this)
        binding.slRvVehiclesInSpentList.setLayoutManager(CarouselLayoutManager())
        binding.slRvVehiclesInSpentList.adapter = vehiclesAdapter
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
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

    private fun getExportFooter(numSpents: Int, totalSpents: Double): String {
        var footer = generateHorizontalDivider()
        val textNumSpents = getString(R.string.spents_numSpents, numSpents)
        val textTotalSpents = getString(R.string.spents_totalSpents, totalSpents)
        footer += "$textNumSpents\n$textTotalSpents\n"
        return footer
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getExportHeader(): String {
        val date = getTimestamp().transformDateIsoToString()
        val selectedVehicle = viewModel.selectedVehicle.value
        var header = "$date ${selectedVehicle!!.brand} ${selectedVehicle.model}\n"
        header += generateHorizontalDivider()
        return header
    }

    private fun generateHorizontalDivider(): String {
        val numScripts = 40
        return "-".repeat(numScripts) + "\n"
    }
}
