package com.heathkornblum.urbandictionary

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class DefinitionsListAdapter(private val definitions: List<WordData>?) : RecyclerView.Adapter<DefinitionsListAdapter.DefinitionsViewHolder>() {

    var defs = definitions;

    /**
     * Extend RecyclerView.ViewHolder
     * @param defView a constraint layout to represent a row of data
     */
    class DefinitionsViewHolder(val defView: ConstraintLayout) : RecyclerView.ViewHolder(defView) {
        val defText : TextView = defView.findViewById(R.id.definitionText)
        val thumbsDownText : TextView = defView.findViewById(R.id.thumbsDownText)
        val thumbsUpText : TextView = defView.findViewById(R.id.thumbsUpText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefinitionsViewHolder {
        val defView = LayoutInflater.from(parent.context)
            .inflate(R.layout.definition_row_item, parent, false) as ConstraintLayout

        return DefinitionsViewHolder(defView)
    }



    override fun onBindViewHolder(holder: DefinitionsViewHolder, position: Int) {

        // Add data to text views
        val defParts = defs?.get(position)
        holder.defText.text = defParts?.definition
        holder.thumbsDownText.text = defParts?.thumbs_down.toString()
        holder.thumbsUpText.text = defParts?.thumbs_up.toString()

        // Altenate row colors
        if (position % 2 == 0) {
            holder.defText.setBackgroundColor(Color.parseColor("#DDDDDD"))
            holder.thumbsDownText.setBackgroundColor(Color.parseColor("#DDDDDD"))
            holder.thumbsUpText.setBackgroundColor(Color.parseColor("#DDDDDD"))
        } else {
            holder.defText.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.thumbsDownText.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.thumbsUpText.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }

    override fun getItemCount(): Int = definitions?.size ?: 0

}