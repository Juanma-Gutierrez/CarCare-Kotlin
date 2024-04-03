package com.juanmaGutierrez.carcare.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.localData.VehicleEntity
import com.juanmaGutierrez.carcare.service.toUpperCamelCase

class VehicleAdapter(private var vehicles: List<VehicleEntity>) :
    RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = vehicles[position]
        holder.bind(vehicle)
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(vehicle: VehicleEntity) {
            val vehicleName =
                "${toUpperCamelCase(vehicle.brand)} ${toUpperCamelCase(vehicle.model)}"
            val plate = "${vehicle.plate.uppercase()}"
            itemView.findViewById<MaterialCheckBox>(R.id.iv_cb_available).isChecked =
                vehicle.available
            itemView.findViewById<MaterialTextView>(R.id.iv_tv_brandAndModel).text = vehicleName
            itemView.findViewById<MaterialTextView>(R.id.iv_tv_plate).text = plate
            itemView.findViewById<ImageView>(R.id.iv_iv_vehicleImage)
                .setImageResource(R.drawable.vehicle_placeholder)
        }
    }

    fun updateData(newVehicles: List<VehicleEntity>) {
        vehicles = newVehicles
        notifyDataSetChanged()
    }
}
