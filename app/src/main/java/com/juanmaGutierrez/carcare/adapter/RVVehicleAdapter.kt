package com.juanmaGutierrez.carcare.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.service.fbGetImageURL
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

class VehicleAdapter(
    private var vehicles: List<VehiclePreview>,
    private val context: Context
) : RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val compactFormat = ConfigService().getPreferencesBoolean(context, Constants.SETTINGS_VEHICLES_LIST_COMPACT)
        val layoutResourceId = if (compactFormat) {
            R.layout.item_vehicle_list
        } else {
            R.layout.item_vehicle_detail
        }
        val view = inflater.inflate(layoutResourceId, parent, false)
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
            if (vehicle.imageURL.isNullOrEmpty() || vehicle.imageURL == "null") {
                itemView.findViewById<ShapeableImageView>(R.id.iv_iv_vehicleImage).setImageResource(
                    when (vehicle.category) {
                        "car" -> R.drawable.placeholder_car
                        "motorcycle" -> R.drawable.placeholder_motorcycle
                        "van" -> R.drawable.placeholder_van
                        "truck" -> R.drawable.placeholder_truck
                        else -> R.drawable.placeholder_vehicle
                    }
                )
            } else {
                vehicle.imageURL?.let {
                    fbGetImageURL(it) { url ->
                        itemView.findViewById<ImageView>(R.id.iv_iv_vehicleImage).load(url)
                    }
                }

            }
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
