package com.heathkornblum.urbandictionary

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class DefinitionsListAdapter(private val definitions: Definitions?) : RecyclerView.Adapter<DefinitionsListAdapter.DefinitionsViewHolder>() {

    val termDefs = definitions?.list

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
        val defParts = termDefs?.get(position)
        holder.defText.text = defParts?.definition
        holder.thumbsDownText.text = defParts?.thumbs_down.toString()
        holder.thumbsUpText.text = defParts?.thumbs_up.toString()
        if (position % 2 == 0) {
            holder.defText.setBackgroundColor(Color.parseColor("#DDDDDD"))
            holder.thumbsDownText.setBackgroundColor(Color.parseColor("#DDDDDD"))
            holder.thumbsUpText.setBackgroundColor(Color.parseColor("#DDDDDD"))
        }
    }

    override fun getItemCount(): Int = definitions?.list?.size ?: 0

}