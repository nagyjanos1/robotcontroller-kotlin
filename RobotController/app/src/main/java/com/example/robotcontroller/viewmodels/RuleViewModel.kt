package com.example.robotcontroller.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.entities.Rule

class RuleViewModel(private val dataSource: AppDatabase) : ViewModel() {
    fun getById(id: Long): Rule? {
        return dataSource.ruleDao().findItemById(id)
    }

    fun getAll(): LiveData<List<Rule>> {
        return dataSource.ruleDao().findAllItems()
    }

    fun insert(name: String,
               baseLimitId: Long,
               baseUniverseId: Long,
               antecedentLimitId: Long,
               antecedentUniverseId: Long,
               fbdlId: Long
    ) {
        val rule = Rule(null, name, baseLimitId, baseUniverseId, antecedentLimitId, antecedentUniverseId, fbdlId)
        dataSource.ruleDao().insertItem(rule)
    }

    fun remove(rule: Rule) {
        dataSource.ruleDao().deleteItem(rule)
    }

    fun update(rule: Rule) {
        dataSource.ruleDao().updateItem(rule)
    }

    fun findItemByIds(ruleIds: List<Long>): List<Rule>? {
        return dataSource.ruleDao().findItemByIds(ruleIds)
    }
}

