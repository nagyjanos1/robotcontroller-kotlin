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

class RuleModel(
    val id: Long?,
    var name: String,
    var baseUniverseId: Long?,
    val baseUniverseName: String,
    var baseLimitId: Long?,
    val baseLimitName: String,
    var antecedentUniverseId: Long?,
    val antecedentUniverseName: String,
    var antecedentLimitId: Long?,
    val antecedentLimitName: String
)

class RuleAdapter(private val onClick: (RuleModel) -> Unit):
    ListAdapter<RuleModel, RuleAdapter.RuleViewHolder>(
        RuleDiffCallback()
    ) {

    private var removableRuleList: MutableList<RuleModel> = mutableListOf()

    fun getRemovableRules(): List<RuleModel> {
        return removableRuleList.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.rule_item_layout,
            parent,
            false
        )
        return RuleViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
        val currentRuleItem = getItem(position)
        holder.bind(currentRuleItem)

        holder.itemView.findViewById<CheckBox>(R.id.deletableListItem).setOnCheckedChangeListener {
                _, isChecked ->
            if (isChecked) {
                removableRuleList.add(currentRuleItem)
            } else {
                removableRuleList.remove(currentRuleItem)
            }
        }
    }

    class RuleViewHolder(itemView: View, val onClick: (RuleModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.ruleListItemName)
        private val baseUniverseNameTextView: TextView = itemView.findViewById(R.id.baseUniverseListItemName)
        private val baseLimitNameTextView: TextView = itemView.findViewById(R.id.baseLimitNameListItem)
        private val antecedentUniverseNameTextView: TextView = itemView.findViewById(R.id.antecedentUniverseListItemName)
        private val antecedentLimitNameTextView: TextView = itemView.findViewById(R.id.antecedentLimitListItemName)
        private var currentRule: RuleModel? = null

        init {
            itemView.setOnClickListener {
                currentRule?.let {
                    onClick(it)
                }
            }
            nameTextView.isEnabled = false
            baseUniverseNameTextView.isEnabled = false
            baseLimitNameTextView.isEnabled = false
            antecedentUniverseNameTextView.isEnabled = false
            antecedentLimitNameTextView.isEnabled = false
        }

        fun bind(rule: RuleModel) {
            currentRule = rule

            nameTextView.text = rule.name
            baseUniverseNameTextView.text = rule.baseUniverseName
            baseLimitNameTextView.text = rule.baseLimitName
            antecedentUniverseNameTextView.text = rule.antecedentUniverseName
            antecedentLimitNameTextView.text = rule.antecedentLimitName
        }
    }

    class RuleDiffCallback : DiffUtil.ItemCallback<RuleModel>() {
        override fun areItemsTheSame(oldItem: RuleModel, newItem: RuleModel): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: RuleModel, newItem: RuleModel): Boolean {
            return oldItem == newItem
        }
    }
}