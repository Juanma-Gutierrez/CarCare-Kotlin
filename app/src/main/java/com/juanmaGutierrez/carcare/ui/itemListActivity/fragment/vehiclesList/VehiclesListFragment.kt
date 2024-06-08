package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.vehiclesList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.adapter.VehicleAdapter
import com.juanmaGutierrez.carcare.databinding.FragmentVehiclesListBinding
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.service.showSnackBar
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

/**
 * Fragment for displaying the list of vehicles.
 */
class VehiclesListFragment : Fragment() {
    private lateinit var binding: FragmentVehiclesListBinding
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var vehiclesList: List<VehiclePreview>
    private lateinit var viewModel: VehiclesListViewModel
    private var step = 0

    /**
     * Called to create the view hierarchy associated with the fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        step = 0
        viewModel = ViewModelProvider(this)[VehiclesListViewModel::class.java]
        binding = FragmentVehiclesListBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored into the view.
     *
     * @param view The view returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureVehicles()
        configureUI()
        viewModel.getFBVehiclesAndSaveFBVehiclesToRoom()
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    override fun onResume() {
        super.onResume()
        step = 0
        binding.vlTlAvailables.getTabAt(0)?.select()
        viewModel.getFBVehiclesAndSaveFBVehiclesToRoom()
    }

    /**
     * Configures the vehicles list and observes changes.
     */
    private fun configureVehicles() {
        viewModel.loadLocalVehicles(requireContext())
        viewModel.vehiclesList.observe(viewLifecycleOwner) { vehiclesList ->
            this.vehiclesList = vehiclesList
            checkSwitchAndUpdateRecylerView()
            step++
            if (step >= 3 && vehiclesList.isEmpty()) {
                showSnackBar(getString(R.string.vehiclesList_noVehicles), requireView()) {}
            }
        }
    }

    /**
     * Configures the UI components.
     */
    private fun configureUI() {
        binding.vlFabAddVehicle.setOnClickListener {
            val ts = ToolbarService.getInstance()
            ts.detailTitle = getString(R.string.new_vehicle)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("fragmentType", "newVehicle")
            startActivity(intent)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            when (isLoading) {
                true -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.VISIBLE
                false -> requireActivity().findViewById<View>(R.id.lottie_isLoading).visibility = View.GONE
            }
        }
        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            showSnackBar(message, this.requireView()) {}
        }
    }

    /**
     * Checks the tab switch and updates the RecyclerView accordingly.
     */
    private fun checkSwitchAndUpdateRecylerView() {
        val tab = binding.vlTlAvailables
        updateRecyclerView(vehiclesList, false)
        tab.addOnTabSelectedListener(object : OnTabSelectedListener {

            /**
             * Called when a tab enters the selected state.
             *
             * @param tab The tab that was selected.
             */
            override fun onTabSelected(tab: Tab?) {
                when (tab?.position) {
                    0 -> updateRecyclerView(vehiclesList, false)
                    1 -> updateRecyclerView(vehiclesList, true)
                    else -> updateRecyclerView(vehiclesList, false)
                }
            }

            /**
             * Called when a tab that is already selected is chosen again by the user.
             *
             * @param tab The tab that was reselected.
             */
            override fun onTabReselected(tab: Tab?) {
                // not implemented
            }

            /**
             * Called when a tab that is already selected becomes unselected.
             *
             * @param tab The tab that was unselected.
             */
            override fun onTabUnselected(tab: Tab?) {
                // not implemented
            }
        })
    }

    /**
     * Updates the RecyclerView with the filtered vehicle list.
     *
     * @param vehiclesList The list of vehicles to display.
     * @param switch A flag indicating whether to filter the list based on availability.
     */
    private fun updateRecyclerView(vehiclesList: List<VehiclePreview>?, switch: Boolean) {
        val filteredList = viewModel.filtercheckAvailablesVehicles(vehiclesList!!, switch)
        vehicleAdapter = VehicleAdapter(filteredList, requireContext())
        vehicleAdapter.updateData(filteredList)
        binding.vlRvVehicles.layoutManager = LinearLayoutManager(requireContext())
        binding.vlRvVehicles.adapter = vehicleAdapter
    }
}