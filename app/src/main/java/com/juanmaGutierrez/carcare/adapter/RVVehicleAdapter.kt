package com.juanmaGutierrez.carcare.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

class VehicleAdapter(private var vehicles: List<VehiclePreview>) : RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {

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
        fun bind(vehicle: VehiclePreview) {
            itemView.findViewById<ShapeableImageView>(R.id.iv_iv_iconCategory).setImageResource(
                when (vehicle.category) {
                    "car" -> R.drawable.icon_vehicle_car
                    "motorcycle" -> R.drawable.icon_vehicle_motorcycle
                    "van" -> R.drawable.icon_vehicle_van
                    "truck" -> R.drawable.icon_vehicle_truck
                    else -> R.drawable.icon_vehicle_car
                }
            )
            itemView.findViewById<MaterialTextView>(R.id.iv_tv_brand).text = vehicle.brand.toUpperCamelCase()
            itemView.findViewById<MaterialTextView>(R.id.iv_tv_Model).text = vehicle.model.toUpperCamelCase()
            itemView.findViewById<MaterialTextView>(R.id.iv_tv_plate).text = vehicle.plate.uppercase()
            itemView.findViewById<ImageView>(R.id.iv_iv_vehicleImage).setImageResource(R.drawable.vehicle_placeholder)
            itemView.findViewById<ShapeableImageView>(R.id.iv_iv_vehicleImage).setImageResource(
                when (vehicle.category) {
                    "car" -> R.drawable.placeholder_car
                    "motorcycle" -> R.drawable.placeholder_motorcycle
                    "van" -> R.drawable.placeholder_van
                    "truck" -> R.drawable.placeholder_truck
                    else -> R.drawable.icon_vehicle_car
                }
            )
            val context = itemView.context
            itemView.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("fragmentType", "editVehicle")
                intent.putExtra("itemID", vehicle.vehicleId)
                context.startActivity(intent)
            }
            if (!vehicle.available) {
                itemView.findViewById<TextView>(R.id.iv_tv_notAvailable).visibility = View.VISIBLE
                itemView.findViewById<ShapeableImageView>(R.id.iv_iv_imageNotAvailable).visibility = View.VISIBLE
            }
        }
    }

    fun updateData(newVehicles: List<VehiclePreview>) {
        vehicles = newVehicles
        notifyDataSetChanged()
    }
}
