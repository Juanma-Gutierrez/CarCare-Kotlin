package com.juanmaGutierrez.carcare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.localData.Provider
import com.juanmaGutierrez.carcare.service.milog

class RVProviderAdapter(
    private var providers: List<Provider>, private val context: Context
) : RecyclerView.Adapter<RVProviderAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerName)
        var category: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerCategory)
        var phone: MaterialTextView = itemView.findViewById(R.id.ip_tv_providerPhone)
        var card: MaterialTextView = itemView.findViewById(R.id.ip_cv_providerItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_provider, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return providers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = providers[position].name
        holder.category.text = providers[position].category
        holder.phone.text = providers[position].phone
        holder.card.setOnClickListener {
            milog("Entra en el listener")
        }
    }
}

