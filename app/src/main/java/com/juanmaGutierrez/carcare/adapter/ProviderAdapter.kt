package com.juanmaGutierrez.carcare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.service.ConfigService
import com.juanmaGutierrez.carcare.service.getProviderCategoryTranslation
import com.juanmaGutierrez.carcare.service.milog

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
        var name: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerName)
        var category: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerCategory)
        var phone: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerPhone)
        var card: MaterialCardView = itemView.findViewById(R.id.ip_cv_providerItem)

        fun bind(provider: Provider) {
            name.text = provider.name
            category.text = provider.category.getProviderCategoryTranslation(itemView.context)
            phone.text = provider.phone
            card.setOnClickListener {
                milog("Entra en el listener ${provider.providerId}")
            }
        }

    }
}

