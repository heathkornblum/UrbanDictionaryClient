package com.heathkornblum.urbandictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DefinitionsListAdapter(private val definitions: Definitions?) : RecyclerView.Adapter<DefinitionsListAdapter.DefinitionsViewHolder>() {

    class DefinitionsViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefinitionsViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.definition, parent, false) as TextView

        return DefinitionsViewHolder(textView)
    }

    override fun onBindViewHolder(holder: DefinitionsViewHolder, position: Int) {
        holder.textView.text = definitions?.list?.get(position)?.definition
    }

    override fun getItemCount(): Int = definitions?.list?.size ?: 0

}