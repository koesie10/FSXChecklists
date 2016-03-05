package com.koenv.fsxchecklists.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.koenv.fsxchecklists.R
import com.koenv.fsxchecklists.bindView
import com.koenv.fsxchecklists.model.ChecklistItem

class ChecklistAdapter(val context: Context, var items: List<ChecklistItem>) : RecyclerView.Adapter<ChecklistAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.nameTextView.text = item.name
        holder.valueTextView.text = item.value

        holder.itemView.setOnClickListener {
            val isChecked = !holder.checkBox.isChecked
            holder.checkBox.isChecked = isChecked
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, if (isChecked) R.color.colorTextChecked else R.color.colorTextPrimary))
            holder.valueTextView.setTextColor(ContextCompat.getColor(context, if (isChecked) R.color.colorTextCheckedAccent else R.color.colorTextPrimary))
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_checklist, parent, false))

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox by bindView<CheckBox>(R.id.checkBox)
        val nameTextView by bindView<TextView>(R.id.nameTextView)
        val valueTextView by bindView<TextView>(R.id.valueTextView)
    }
}