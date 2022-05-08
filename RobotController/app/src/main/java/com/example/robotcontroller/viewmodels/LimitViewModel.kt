package com.example.robotcontroller.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.entities.Limit

class LimitViewModel(private val dataSource: AppDatabase) : ViewModel() {
    fun getById(id: Long): Limit? {
        return dataSource.limitsDao().findItemById(id)
    }

    fun getAll(): LiveData<List<Limit>> {
        return dataSource.limitsDao().findAllItems()
    }

    fun insert(name: String,
               min: Int,
               max: Int,
               universeId: Long,
               fbdlId: Long
    ) {
        val limit = Limit(null, name, min, max, universeId, fbdlId)
        dataSource.limitsDao().insertItem(limit)
    }

    fun update(limit: Limit) {
        dataSource.limitsDao().updateItem(limit)
    }

    fun remove(limit: Limit) {
        dataSource.limitsDao().deleteItem(limit)
    }

    fun findItemByIds(limitIds: List<Long>): Array<Limit>? {
        return dataSource.limitsDao().findItemByIds(limitIds)
    }

    fun getAllByUniverseId(universeId: Long): List<Limit> {
        return dataSource.limitsDao().findItemByUniverseId(universeId)
    }
}