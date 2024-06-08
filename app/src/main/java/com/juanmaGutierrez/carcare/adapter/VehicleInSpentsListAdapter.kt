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

/**
 * Interface for handling clicks on vehicle items in the list
 */
fun interface OnVehicleClickListener {
    /**
     * Called when a vehicle item is clicked
     * @param vehicle: The clicked vehicle
     */
    fun onVehicleClick(vehicle: VehiclePreview)
}

/**
 * Adapter for displaying vehicles in a RecyclerView within the spent items list
 * @param vehicles: List of vehicles to display
 * @param context: Context of the application
 */
class VehicleInSpentsListAdapter(
    private val vehicles: List<VehiclePreview>, private val context: Context
) : RecyclerView.Adapter<VehicleInSpentsListAdapter.ViewHolder>() {
    private var onVehicleClickListener: OnVehicleClickListener? = null

    /**
     * Creates and returns a ViewHolder object, inflating the appropriate layout
     * @param parent: The parent ViewGroup into which the new view will be added after it is bound to an adapter position
     * @param viewType: The view type of the new view
     * @return ViewHolder: A new ViewHolder that holds a view of the given view type
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleInSpentsListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_vehicle_in_spents, parent, false)
        return ViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position
     * @param holder: The ViewHolder which should be updated to represent the contents of the item at the given position
     * @param position: The position of the item within the adapter's data set
     */
    override fun onBindViewHolder(holder: VehicleInSpentsListAdapter.ViewHolder, position: Int) {
        holder.bind(vehicles[position])
    }

    /**
     * Returns the total number of items in the data set held by the adapter
     * @return Int: The total number of items in this adapter
     */
    override fun getItemCount(): Int {
        return vehicles.size
    }

    /**
     * Sets the listener for handling clicks on vehicle items
     * @param listener: The listener to set
     */
    fun setOnVehicleClickListener(listener: OnVehicleClickListener) {
        onVehicleClickListener = listener
    }

    /**
     * ViewHolder class for the vehicle items
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val brand: MaterialTextView = itemView.findViewById(R.id.ivs_tv_brand)
        private val model: MaterialTextView = itemView.findViewById(R.id.ivs_tv_model)
        private val image: ImageView = itemView.findViewById(R.id.ivs_iv_vehicleImage)
        private val card: MaskableFrameLayout = itemView.findViewById(R.id.ivs_mf_vehicleItemCarousel)
        private val container: LinearLayout = itemView.findViewById(R.id.ivs_ll_vehicleTitleContainer)

        /**
         * Binds the vehicle data to the view
         * @param vehicle: The vehicle data to bind
         */
        fun bind(vehicle: VehiclePreview) {
            configureTitleAndBrand(vehicle)
            configureImage(vehicle)
            configureAvailable(vehicle)
            configureOnClickListeners(vehicle)
        }

        /**
         * Configures the title and brand for the vehicle item
         * @param vehicle: The vehicle data to use
         */
        private fun configureTitleAndBrand(vehicle: VehiclePreview) {
            brand.text = vehicle.brand
            model.text = vehicle.model
        }

        /**
         * Configures the image for the vehicle item
         * @param vehicle: The vehicle data to use
         */
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

        /**
         * Configures the display for unavailable vehicles
         * @param vehicle: The vehicle data to use
         */
        private fun configureAvailable(vehicle: VehiclePreview) {
            if (!vehicle.available) {
                val colorNotAvailable = ContextCompat.getColor(context, R.color.transparent_bg_error)
                container.setBackgroundColor(colorNotAvailable)
            }
        }

        /**
         * Configures the click listener for the vehicle item
         * @param vehicle: The vehicle data to use
         */
        private fun configureOnClickListeners(vehicle: VehiclePreview) {
            card.setOnClickListener { onVehicleClickListener?.onVehicleClick(vehicle) }
        }
    }
}