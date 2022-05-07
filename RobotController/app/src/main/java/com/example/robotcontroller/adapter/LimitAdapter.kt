package com.example.robotcontroller.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.robotcontroller.R
import com.example.robotcontroller.data.entities.Limit

class LimitAdapter(private val onClick: (Limit) -> Unit):
    ListAdapter<Limit, LimitAdapter.LimitViewHolder>(
        LimitDiffCallback()
    ) {

    private var removableLimitList: MutableList<Limit> = mutableListOf()

    fun getRemovableLimits(): List<Limit> {
        return removableLimitList.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LimitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.limit_item_layout,
            parent,
            false
        )
        return LimitViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: LimitViewHolder, position: Int) {
        val currentLimitItem = getItem(position)
        holder.bind(currentLimitItem, position)

        holder.itemView.findViewById<CheckBox>(R.id.deletableListItem).setOnCheckedChangeListener {
                _, isChecked ->
            if (isChecked) {
                removableLimitList.add(currentLimitItem)
            } else {
                removableLimitList.remove(currentLimitItem)
            }
        }
    }

    class LimitViewHolder(itemView: View, val onClick: (Limit) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val minValueTextView: TextView = itemView.findViewById(R.id.editLimitMinValue)
        private val maxValueTextView: TextView = itemView.findViewById(R.id.editLimitMaxValue)
        private var currentLimit: Limit? = null

        init {
            itemView.setOnClickListener {
                currentLimit?.let {
                    onClick(it)
                }
            }
            minValueTextView.isEnabled = false;
            maxValueTextView.isEnabled = false;
        }

        fun bind(limit: Limit, position: Int) {
            currentLimit = limit

            minValueTextView.text = limit.minValue.toString()
            maxValueTextView.text = limit.maxValue.toString()
        }
    }

    class LimitDiffCallback : DiffUtil.ItemCallback<Limit>() {
        override fun areItemsTheSame(oldItem: Limit, newItem: Limit): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Limit, newItem: Limit): Boolean {
            return oldItem == newItem
        }
    }
}