package com.example.robotcontroller.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.RuleBase

class RuleBaseViewModel(val dataSource: AppDatabase) : ViewModel() {
    fun getById(id: Long): RuleBase? {
        return dataSource.ruleBaseDao().findItemById(id)
    }

    fun getAll(): LiveData<List<RuleBase>> {
        return dataSource.ruleBaseDao().findAllItems()
    }

    fun insert(name: String, fbdlId: Long, universeId: Long) {
        val ruleBase = RuleBase(null, name, fbdlId, universeId)
        dataSource.ruleBaseDao().insertItem(ruleBase)
    }

    fun remove(ruleBase: RuleBase) {
        dataSource.ruleBaseDao().deleteItem(ruleBase)
    }
}