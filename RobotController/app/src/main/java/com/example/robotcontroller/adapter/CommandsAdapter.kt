package com.example.robotcontroller.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.robotcontroller.adapter.CommandsAdapter.*
import com.example.robotcontroller.data.entities.FbdlCommandItem
import androidx.recyclerview.widget.ListAdapter
import com.example.robotcontroller.R
import com.example.robotcontroller.R.layout.activity_command_card

class CommandsAdapter(private val onClick: (FbdlCommandItem) -> Unit):
    ListAdapter<FbdlCommandItem, CommandViewHolder>(FbdlListItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            activity_command_card,
            parent,
            false
        )
        return CommandViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: CommandViewHolder, position: Int) {
        val currentFbdlCommandItem = getItem(position)
        holder.bind(currentFbdlCommandItem)
    }

    class CommandViewHolder(itemView: View, val onClick: (FbdlCommandItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.name_card)
        val fbdlCommand: TextView = itemView.findViewById(R.id.fbdlCommand_card)
        val isDefaultCommand: CheckBox = itemView.findViewById(R.id.is_default_card)

        private var currentFbdlCommandItem: FbdlCommandItem? = null

        init {
            itemView.setOnClickListener {
                currentFbdlCommandItem?.let {
                    onClick(it)
                }
            }
        }

        fun bind(commandItem: FbdlCommandItem) {
            currentFbdlCommandItem = commandItem

            name.text = commandItem.name
            fbdlCommand.text = commandItem.description
            isDefaultCommand.isChecked = commandItem.isDefault
        }
    }

    class FbdlListItemDiffCallback : DiffUtil.ItemCallback<FbdlCommandItem>() {
        override fun areItemsTheSame(oldItem: FbdlCommandItem, newItem: FbdlCommandItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: FbdlCommandItem, newItem: FbdlCommandItem): Boolean {
            return oldItem == newItem
        }
    }
}
