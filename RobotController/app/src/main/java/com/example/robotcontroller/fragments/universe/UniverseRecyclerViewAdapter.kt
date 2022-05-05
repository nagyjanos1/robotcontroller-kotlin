package com.example.robotcontroller.fragments.universe

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.robotcontroller.data.Universe
import com.example.robotcontroller.databinding.FragmentUniverseBinding

import com.example.robotcontroller.fragments.universe.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class UniverseRecyclerViewAdapter(
    private val values: List<Universe>?
) : RecyclerView.Adapter<UniverseRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentUniverseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values?.get(position)
        holder.idView.text = item?.id.toString()
        holder.contentView.text = item?.name
    }

    override fun getItemCount(): Int = values?.size!!

    inner class ViewHolder(binding: FragmentUniverseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}