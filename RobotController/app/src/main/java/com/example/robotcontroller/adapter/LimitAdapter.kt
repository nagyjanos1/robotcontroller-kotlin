package com.example.robotcontroller.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.robotcontroller.R

class LimitModel(
    val id: Long?,
    var name: String,
    var minValue: Int,
    var maxValue: Int,
    var universeId: Long?,
    val universeName: String,
    val fbdlId: Long?
)

class LimitAdapter(private val onClick: (LimitModel) -> Unit):
    ListAdapter<LimitModel, LimitAdapter.LimitViewHolder>(
        LimitDiffCallback()
    ) {

    private var removableLimitList: MutableList<LimitModel> = mutableListOf()

    fun getRemovableLimits(): List<LimitModel> {
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

    class LimitViewHolder(itemView: View, val onClick: (LimitModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val minValueTextView: TextView = itemView.findViewById(R.id.limitMinValueListItem)
        private val maxValueTextView: TextView = itemView.findViewById(R.id.limitMaxValueListItem)
        private val nameTextView: TextView = itemView.findViewById(R.id.limitNameListItem)
        private val universeNameTextView: TextView = itemView.findViewById(R.id.universeListItemName)
        private var currentLimit: LimitModel? = null

        init {
            itemView.setOnClickListener {
                currentLimit?.let {
                    onClick(it)
                }
            }
            minValueTextView.isEnabled = false;
            maxValueTextView.isEnabled = false;
        }

        fun bind(limit: LimitModel, position: Int) {
            currentLimit = limit

            minValueTextView.text = limit.minValue.toString()
            maxValueTextView.text = limit.maxValue.toString()
            nameTextView.text = limit.name
            universeNameTextView.text = limit.universeName
        }
    }

    class LimitDiffCallback : DiffUtil.ItemCallback<LimitModel>() {
        override fun areItemsTheSame(oldItem: LimitModel, newItem: LimitModel): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: LimitModel, newItem: LimitModel): Boolean {
            return oldItem == newItem
        }
    }
}