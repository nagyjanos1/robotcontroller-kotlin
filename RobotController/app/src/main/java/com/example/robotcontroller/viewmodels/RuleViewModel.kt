package com.example.robotcontroller.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.entities.Rule

class RuleViewModel(val dataSource: AppDatabase) : ViewModel() {
    fun getById(id: Long): Rule? {
        return dataSource.ruleDao().findItemById(id)
    }

    fun getAll(): LiveData<List<Rule>> {
        return dataSource.ruleDao().findAllItems()
    }

    fun insert(name: String,
               baseLimitId: Long,
               baseUnverseId: Long,
               ruleLimitId: Long,
               ruleUniverseId: Long
    ) {
        val rule = Rule(null, name, baseLimitId, baseUnverseId, ruleLimitId, ruleUniverseId)
        dataSource.ruleDao().insertItem(rule)
    }

    fun remove(rule: Rule) {
        dataSource.ruleDao().deleteItem(rule)
    }
}

