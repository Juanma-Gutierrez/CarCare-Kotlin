package com.juanmaGutierrez.carcare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.localData.VehicleEntity

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
            itemView.findViewById<MaterialTextView>(R.id.iv_tv_brandAndModel).text =
                "${vehicle.brand.uppercase()} ${vehicle.model.uppercase()}"
            itemView.findViewById<MaterialTextView>(R.id.iv_tv_plate).text = "${vehicle.plate}"
        }
    }

    fun updateData(newVehicles: List<VehicleEntity>) {
        vehicles = newVehicles
        notifyDataSetChanged()
    }
}
