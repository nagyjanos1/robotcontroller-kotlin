package com.example.robotcontroller.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.robotcontroller.R
import com.example.robotcontroller.data.entities.Universe

class UniverseAdapter(private val onClick: (Universe) -> Unit):
    ListAdapter<Universe, UniverseAdapter.UniverseViewHolder>(
        UniverseDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniverseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.universe_item_layout,
            parent,
            false
        )
        return UniverseViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: UniverseViewHolder, position: Int) {
        val currentUniverseItem = getItem(position)
        holder.bind(currentUniverseItem, position)
    }

    class UniverseViewHolder(itemView: View, val onClick: (Universe) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.universeListItemName)

        private var currentUniverse: Universe? = null

        init {
            itemView.setOnClickListener {
                currentUniverse?.let {
                    onClick(it)
                }
            }

            nameTextView.isEnabled = false;
        }

        fun bind(universe: Universe, position: Int) {
            currentUniverse = universe

            nameTextView.text = universe.name
        }
    }

    class UniverseDiffCallback : DiffUtil.ItemCallback<Universe>() {
        override fun areItemsTheSame(oldItem: Universe, newItem: Universe): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Universe, newItem: Universe): Boolean {
            return oldItem == newItem
        }
    }
}