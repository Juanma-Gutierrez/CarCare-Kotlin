package com.juanmaGutierrez.carcare.adapter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.AlertDialogModel
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.service.getProviderCategoryTranslation
import com.juanmaGutierrez.carcare.service.showDialogAcceptCancel
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

/**
 * Adapter for displaying a list of providers in a RecyclerView
 * @param providers: List of providers to display
 * @param context: Context of the application
 */
class ProviderAdapter(
    private var providers: List<Provider>, private val context: Context
) : RecyclerView.Adapter<ProviderAdapter.ViewHolder>() {

    /**
     * Creates and returns a ViewHolder object, inflating a standard layout called item_provider_list.
     * @param parent: The parent ViewGroup into which the new view will be added after it is bound to an adapter position
     * @param viewType: The view type of the new view
     * @return ViewHolder: A new ViewHolder that holds a view of the given view type
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val providersGridFormat =
            ConfigService().getPreferencesBoolean(context, Constants.SETTINGS_PROVIDERS_GRID_FORMAT)
        val layoutResourceId = if (providersGridFormat) {
            R.layout.item_provider_grid
        } else {
            R.layout.item_provider_list
        }
        val view = inflater.inflate(layoutResourceId, parent, false)
        return ViewHolder(view)
    }

    /**
     * Returns the total number of items in the data set held by the adapter
     * @return Int: The total number of items in this adapter
     */
    override fun getItemCount(): Int {
        return providers.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position
     * @param holder: The ViewHolder which should be updated to represent the contents of the item at the given position
     * @param position: The position of the item within the adapter's data set
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(providers[position])
    }

    /**
     * ViewHolder class for the provider items
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerName)
        private val category: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerCategory)
        private val phone: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerPhone)
        private val card: MaterialCardView = itemView.findViewById(R.id.ip_cv_providerItem)
        private val icon: ImageView = itemView.findViewById(R.id.ip_iv_providerIcon)
        private val phoneButton: ImageView = itemView.findViewById(R.id.ip_iv_phoneButton)

        /**
         * Binds the provider data to the view
         * @param provider: The provider data to bind
         */
        fun bind(provider: Provider) {
            configureData(provider)
            configureIcon(provider)
            configureListeners(provider)
            configurePhoneIcon(provider)
        }

        /**
         * Configures the phone icon visibility based on whether the provider has a phone number
         * @param provider: The provider data to check
         */
        private fun configurePhoneIcon(provider: Provider) {
            if (provider.phone.isEmpty()) {
                phoneButton.visibility = View.GONE
            }
        }

        /**
         * Configures the display data for the provider
         * @param provider: The provider data to display
         */
        private fun configureData(provider: Provider) {
            name.text = provider.name.toUpperCamelCase()
            category.text = provider.category.getProviderCategoryTranslation(itemView.context).toUpperCamelCase()
            phone.text = provider.phone.toUpperCamelCase()
        }

        /**
         * Configures the icon based on the provider's category
         * @param provider: The provider data to check
         */
        private fun configureIcon(provider: Provider) {
            icon.setImageResource(
                when (provider.category) {
                    "workshop" -> R.drawable.icon_provider_category_workshop
                    "gasStation" -> R.drawable.icon_provider_category_gas_station
                    "insuranceCompany" -> R.drawable.icon_provider_category_insurance_company
                    "ITV" -> R.drawable.icon_provider_category_itv
                    "towTruck" -> R.drawable.icon_provider_category_tow_truck
                    "other" -> R.drawable.icon_provider_category_other
                    else -> {
                        R.drawable.icon_provider_category_gas_station
                    }
                }
            )
        }

        /**
         * Configures the listeners for the provider item
         * @param provider: The provider data to use
         */
        private fun configureListeners(provider: Provider) {
            configureCardListener(provider)
            if (provider.phone.isNotEmpty()) {
                configurePhoneButtonListener(provider)
            }
        }

        /**
         * Configures the card click listener to open the detail activity
         * @param provider: The provider data to use
         */
        private fun configureCardListener(provider: Provider) {
            card.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("fragmentType", "editProvider")
                intent.putExtra("itemId", provider.providerId)
                context.startActivity(intent)
            }
        }

        /**
         * Configures the phone button click listener to initiate a call
         * @param provider: The provider data to use
         */
        private fun configurePhoneButtonListener(provider: Provider) {
            phoneButton.setOnClickListener {
                val phoneNumber = provider.phone
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$phoneNumber")
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val title = itemView.context.getString(R.string.alertDialog_confirm_message)
                    val message =
                        itemView.context.getString(R.string.alertDialog_provider_confirm_call, provider.name)
                    val icon = AppCompatResources.getDrawable(itemView.context, R.drawable.icon_phone)
                    val ad = AlertDialogModel(context as Activity, title, message, icon)
                    showDialogAcceptCancel(ad) { accept ->
                        if (accept) {
                            context.startActivity(callIntent)
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE), 1)
                }
            }
        }
    }
}

