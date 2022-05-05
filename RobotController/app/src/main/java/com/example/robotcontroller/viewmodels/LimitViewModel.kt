package com.example.robotcontroller.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.entities.Limit

class LimitViewModel(val dataSource: AppDatabase) : ViewModel() {
    fun getById(id: Long): Limit? {
        return dataSource.limitsDao().findItemById(id)
    }

    fun getAll(): LiveData<List<Limit>> {
        return dataSource.limitsDao().findAllItems()
    }

    fun insert(name: String,
               min: Long,
               max: Long,
                universeId: Long
    ) {
        val limit = Limit(null, name, min, max, universeId)
        dataSource.limitsDao().insertItem(limit)
    }

    fun remove(limit: Limit) {
        dataSource.limitsDao().deleteItem(limit)
    }
}