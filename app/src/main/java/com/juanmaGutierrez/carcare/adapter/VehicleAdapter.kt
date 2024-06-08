package com.juanmaGutierrez.carcare.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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

/**
 * Adapter for displaying a list of vehicles in a RecyclerView
 * @param vehicles: List of vehicles to display
 * @param context: Context of the application
 */
class VehicleAdapter(
    private var vehicles: List<VehiclePreview>, private val context: Context
) : RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {

    /**
     * Creates and returns a ViewHolder object, inflating the appropriate layout based on user preferences
     * @param parent: The parent ViewGroup into which the new view will be added after it is bound to an adapter position
     * @param viewType: The view type of the new view
     * @return ViewHolder: A new ViewHolder that holds a view of the given view type
     */
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

    /**
     * Called by RecyclerView to display the data at the specified position
     * @param holder: The ViewHolder which should be updated to represent the contents of the item at the given position
     * @param position: The position of the item within the adapter's data set
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = vehicles[position]
        holder.bind(vehicle)
    }

    /**
     * Returns the total number of items in the data set held by the adapter
     * @return Int: The total number of items in this adapter
     */
    override fun getItemCount(): Int {
        return vehicles.size
    }

    /**
     * ViewHolder class for the vehicle items
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ShapeableImageView = itemView.findViewById(R.id.iv_iv_iconCategory)
        private val brand: MaterialTextView = itemView.findViewById(R.id.iv_tv_brand)
        private val model: MaterialTextView = itemView.findViewById(R.id.iv_tv_Model)
        private val plate: MaterialTextView = itemView.findViewById(R.id.iv_tv_plate)
        private val vehicleImage: ShapeableImageView = itemView.findViewById(R.id.iv_iv_vehicleImage)

        /**
         * Binds the vehicle data to the view
         * @param vehicle: The vehicle data to bind
         */
        fun bind(vehicle: VehiclePreview) {
            configureIcon(vehicle)
            configureData(vehicle)
            configureImage(vehicle)
            configureListeners(vehicle)
            configureNotAvailable(vehicle)
        }

        /**
         * Configures the display icon for the vehicle category
         * @param vehicle: The vehicle data to use
         */
        private fun configureIcon(vehicle: VehiclePreview) {
            icon.setImageResource(
                when (vehicle.category) {
                    "car" -> R.drawable.icon_vehicle_car
                    "motorcycle" -> R.drawable.icon_vehicle_motorcycle
                    "van" -> R.drawable.icon_vehicle_van
                    "truck" -> R.drawable.icon_vehicle_truck
                    else -> R.drawable.icon_vehicle_car
                }
            )
        }

        /**
         * Configures the display data for the vehicle
         * @param vehicle: The vehicle data to display
         */
        private fun configureData(vehicle: VehiclePreview) {
            brand.text = vehicle.brand.toUpperCamelCase()
            model.text = vehicle.model.toUpperCamelCase()
            plate.text = vehicle.plate.uppercase()
        }

        /**
         * Configures the display image for the vehicle
         * @param vehicle: The vehicle data to use
         */
        private fun configureImage(vehicle: VehiclePreview) {
            if (vehicle.imageURL.isNullOrEmpty() || vehicle.imageURL == "null") {
                vehicleImage.setImageResource(
                    when (vehicle.category) {
                        "car" -> R.drawable.placeholder_car
                        "motorcycle" -> R.drawable.placeholder_motorcycle
                        "van" -> R.drawable.placeholder_van
                        "truck" -> R.drawable.placeholder_truck
                        else -> R.drawable.placeholder_vehicle
                    }
                )
            } else {
                vehicle.imageURL?.let { fbGetImageURL(it) { url -> vehicleImage.load(url) } }
            }
        }

        /**
         * Configures the click listeners for the vehicle item
         * @param vehicle: The vehicle data to use
         */
        private fun configureListeners(vehicle: VehiclePreview) {
            val context = itemView.context
            itemView.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("fragmentType", "editVehicle")
                intent.putExtra("itemId", vehicle.vehicleId)
                context.startActivity(intent)
            }
        }

        /**
         * Configures the display for unavailable vehicles
         * @param vehicle: The vehicle data to use
         */
        private fun configureNotAvailable(vehicle: VehiclePreview) {
            if (!vehicle.available) {
                itemView.findViewById<TextView>(R.id.iv_tv_notAvailable).visibility = View.VISIBLE
                val colorNotAvailable = ContextCompat.getColor(context, R.color.transparent_bg_error)
                itemView.findViewById<ConstraintLayout>(R.id.iv_cl_vehicleTitleContainer)
                    .setBackgroundColor(colorNotAvailable)
            }
        }
    }

    /**
     * Updates the adapter's data with a new list of vehicles
     * @param newVehicles: The new list of vehicles to display
     */
    fun updateData(newVehicles: List<VehiclePreview>) {
        vehicles = newVehicles
        notifyDataSetChanged()
    }
}
