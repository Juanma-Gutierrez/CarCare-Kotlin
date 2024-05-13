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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.service.getProviderCategoryTranslation
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

class ProviderAdapter(
    private var providers: List<Provider>, private val context: Context
) : RecyclerView.Adapter<ProviderAdapter.ViewHolder>() {

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

    override fun getItemCount(): Int {
        return providers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(providers[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerName)
        private val category: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerCategory)
        private val phone: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerPhone)
        private val card: MaterialCardView = itemView.findViewById(R.id.ip_cv_providerItem)
        private val icon: ImageView = itemView.findViewById(R.id.ip_iv_providerIcon)
        private val phoneButton: ImageView = itemView.findViewById(R.id.ip_iv_phoneButton)

        fun bind(provider: Provider) {
            configureData(provider)
            configureIcon(provider)
            configureListeners(provider)
            configurePhoneIcon(provider)
        }

        private fun configurePhoneIcon(provider: Provider) {
            if (provider.phone.isEmpty()) {
                phoneButton.visibility = View.GONE
            }
        }

        private fun configureData(provider: Provider) {
            name.text = provider.name.toUpperCamelCase()
            category.text = provider.category.getProviderCategoryTranslation(itemView.context).toUpperCamelCase()
            phone.text = provider.phone.toUpperCamelCase()
        }

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

        private fun configureListeners(provider: Provider) {
            configureCardListener(provider)
            if (provider.phone.isNotEmpty()) {
                configurePhoneButtonListener(provider)
            }
        }

        private fun configureCardListener(provider: Provider) {
            card.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("fragmentType", "editProvider")
                intent.putExtra("itemID", provider.providerId)
                context.startActivity(intent)
            }
        }

        private fun configurePhoneButtonListener(provider: Provider) {
            phoneButton.setOnClickListener {
                val phoneNumber = provider.phone
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$phoneNumber")
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    context.startActivity(callIntent)
                } else {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE), 1)
                }
            }
        }
    }
}

