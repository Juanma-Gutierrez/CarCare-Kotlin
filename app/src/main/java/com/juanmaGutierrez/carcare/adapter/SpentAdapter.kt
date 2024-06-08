package com.juanmaGutierrez.carcare.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.service.euroFormat
import com.juanmaGutierrez.carcare.service.toCapitalizeString
import com.juanmaGutierrez.carcare.service.toUpperCamelCase
import com.juanmaGutierrez.carcare.service.transformDateIsoToString
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

/**
 * Adapter for displaying a list of expenses (spents) in a RecyclerView
 * @param spents: List of expenses to display
 * @param context: Context of the application
 * @param vehicleId: ID of the vehicle associated with the expenses
 */
class SpentAdapter(private var spents: List<SpentFB>, private val context: Context, private val vehicleId: String) :
    RecyclerView.Adapter<SpentAdapter.ViewHolder>() {

    /**
     * Creates and returns a ViewHolder object, inflating a standard layout called item_spent_list.
     * @param parent: The parent ViewGroup into which the new view will be added after it is bound to an adapter position
     * @param viewType: The view type of the new view
     * @return ViewHolder: A new ViewHolder that holds a view of the given view type
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_spent_list, parent, false)
        return ViewHolder(view)
    }

    /**
     * Returns the total number of items in the data set held by the adapter
     * @return Int: The total number of items in this adapter
     */
    override fun getItemCount(): Int {
        return spents.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position
     * @param holder: The ViewHolder which should be updated to represent the contents of the item at the given position
     * @param position: The position of the item within the adapter's data set
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(spents[position])
    }

    /**
     * ViewHolder class for the expense items
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: MaterialTextView = itemView.findViewById(R.id.si_tv_providerName)
        private val date: MaterialTextView = itemView.findViewById(R.id.si_tv_spentDate)
        private val observations: MaterialTextView = itemView.findViewById(R.id.si_tv_spentObservations)
        private val amount: MaterialTextView = itemView.findViewById(R.id.si_tv_spentAmount)
        private val card: MaterialCardView = itemView.findViewById(R.id.si_cv_spentItem)

        /**
         * Binds the expense data to the view
         * @param spent: The expense data to bind
         */
        fun bind(spent: SpentFB) {
            configureData(spent)
            configureListeners(spent)
        }

        /**
         * Configures the display data for the expense
         * @param spent: The expense data to display
         */
        private fun configureData(spent: SpentFB) {
            name.text = spent.providerName.toUpperCamelCase()
            date.text = spent.date.transformDateIsoToString()
            observations.text = spent.observations.toCapitalizeString()
            amount.text = spent.amount.euroFormat()
        }

        /**
         * Configures the listeners for the expense item
         * @param spent: The expense data to use
         */
        private fun configureListeners(spent: SpentFB) {
            card.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("fragmentType", "editSpent")
                intent.putExtra("vehicleId", vehicleId)
                intent.putExtra("itemId", spent.spentId)
                context.startActivity(intent)
            }
        }
    }
}
