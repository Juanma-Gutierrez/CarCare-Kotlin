package com.juanmaGutierrez.carcare.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.service.milog
import com.juanmaGutierrez.carcare.service.transformDateIsoToString
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

class SpentAdapter(private var spents: List<SpentFB>, private val context: Context, private val vehicleId: String) :
    RecyclerView.Adapter<SpentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_spent_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return spents.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(spents[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: MaterialTextView = itemView.findViewById(R.id.si_tv_providerName)
        private val date: MaterialTextView = itemView.findViewById(R.id.si_tv_spentDate)
        private val observations: MaterialTextView = itemView.findViewById(R.id.si_tv_spentObservations)
        private val amount: MaterialTextView = itemView.findViewById(R.id.si_tv_spentAmount)
        private val card: MaterialCardView = itemView.findViewById(R.id.si_cv_spentItem)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(spent: SpentFB) {
            configureData(spent)
            configureListeners(spent)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun configureData(spent: SpentFB) {
            name.text = spent.providerName
            date.text = spent.date.transformDateIsoToString()
            observations.text = spent.observations
            amount.text = String.format("%.2f €", spent.amount)
        }

        private fun configureListeners(spent: SpentFB) {
            card.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("fragmentType", "editSpent")
                intent.putExtra("vehicleID", vehicleId)
                intent.putExtra("itemID", spent.spentId)
                context.startActivity(intent)
            }
        }
    }
}
