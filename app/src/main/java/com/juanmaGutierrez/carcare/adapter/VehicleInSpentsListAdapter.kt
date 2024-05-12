package com.juanmaGutierrez.carcare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.fbGetImageURL
import com.juanmaGutierrez.carcare.service.milog

class VehicleInSpentsListAdapter(
    private val vehicles: List<VehiclePreview>, private val context: Context
) : RecyclerView.Adapter<VehicleInSpentsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleInSpentsListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_vehicle_in_spents, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleInSpentsListAdapter.ViewHolder, position: Int) {
        holder.bind(vehicles[position])
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val brand: MaterialTextView = itemView.findViewById(R.id.ivs_tv_brand)
        private val model: MaterialTextView = itemView.findViewById(R.id.ivs_tv_model)
        private val image: ImageView = itemView.findViewById(R.id.ivs_iv_vehicleImage)
        private val card: CardView = itemView.findViewById(R.id.ivs_cv_vehicleInSpentsListCard)

        fun bind(vehicle: VehiclePreview) {
            brand.setText(vehicle.brand)
            model.setText(vehicle.model)
            vehicle.imageURL?.let { fbGetImageURL(it) { url -> image.load(url) } }
            card.setOnClickListener {
                milog("Pulsado: ${vehicle.vehicleId}")
            }
        }
    }
}