package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.vehicle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.service.showSnackBar

class VehicleDetailFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vehicle_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDataInVehiclesCategory()
    }

    private fun loadDataInVehiclesCategory() {
        val acCategory: AutoCompleteTextView = requireView().findViewById(R.id.vd_ac_category)
        val categories = ArrayList<String>()
        categories.add(this.getString(R.string.vehicle_category_car))
        categories.add(this.getString(R.string.vehicle_category_motorcycle))
        categories.add(this.getString(R.string.vehicle_category_van))
        categories.add(this.getString(R.string.vehicle_category_truck))
        val categoryAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, categories)
        acCategory.setAdapter(categoryAdapter)
        acCategory.setOnItemClickListener { _, _, _, id -> showSnackBar(categories[id.toInt()], requireView()) }
    }
}