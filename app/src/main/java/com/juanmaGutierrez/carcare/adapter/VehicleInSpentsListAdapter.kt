package com.juanmaGutierrez.carcare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.carousel.MaskableFrameLayout
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.localData.VehiclePreview
import com.juanmaGutierrez.carcare.service.fbGetImageURL

interface OnVehicleClickListener {
    fun onVehicleClick(vehicle: VehiclePreview)
}

class VehicleInSpentsListAdapter(
    private val vehicles: List<VehiclePreview>, private val context: Context
) : RecyclerView.Adapter<VehicleInSpentsListAdapter.ViewHolder>() {

    private var onVehicleClickListener: OnVehicleClickListener? = null

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

    fun setOnVehicleClickListener(listener: OnVehicleClickListener) {
        onVehicleClickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val brand: MaterialTextView = itemView.findViewById(R.id.ivs_tv_brand)
        private val model: MaterialTextView = itemView.findViewById(R.id.ivs_tv_model)
        private val image: ImageView = itemView.findViewById(R.id.ivs_iv_vehicleImage)
        private val card: MaskableFrameLayout = itemView.findViewById(R.id.ivs_mf_vehicleItemCarousel)
        private val container: LinearLayout = itemView.findViewById(R.id.ivs_ll_vehicleTitleContainer)

        fun bind(vehicle: VehiclePreview) {
            configureTitleAndBrand(vehicle)
            configureImage(vehicle)
            configureAvailable(vehicle)
            configureOnClickListeners(vehicle)
        }

        private fun configureTitleAndBrand(vehicle: VehiclePreview) {
            brand.setText(vehicle.brand)
            model.setText(vehicle.model)
        }

        private fun configureImage(vehicle: VehiclePreview) {
            if (vehicle.imageURL.isNullOrEmpty()) {
                when (vehicle.category) {
                    "car" -> image.load(context.getDrawable(R.drawable.placeholder_car))
                    "motorcycle" -> image.load(context.getDrawable(R.drawable.placeholder_motorcycle))
                    "van" -> image.load(context.getDrawable(R.drawable.placeholder_van))
                    "truck" -> image.load(context.getDrawable(R.drawable.placeholder_truck))
                }
            } else {
                vehicle.imageURL?.let { fbGetImageURL(it) { url -> image.load(url) } }
            }
        }

        private fun configureAvailable(vehicle: VehiclePreview) {
            if (!vehicle.available) {
                val colorNotAvailable = ContextCompat.getColor(context, R.color.transparent_bg_error)
                container.setBackgroundColor(colorNotAvailable)
            }
        }


        private fun configureOnClickListeners(vehicle: VehiclePreview) {
            card.setOnClickListener { onVehicleClickListener?.onVehicleClick(vehicle) }
        }
    }
}