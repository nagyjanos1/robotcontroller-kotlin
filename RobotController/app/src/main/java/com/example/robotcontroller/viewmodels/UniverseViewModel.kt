package com.example.robotcontroller.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.entities.Universe

class UniverseViewModel(val dataSource: AppDatabase) : ViewModel() {
    fun getById(id: Long): Universe? {
        return dataSource.universeDao().findItemById(id)
    }

    fun getAll(): LiveData<List<Universe>> {
        return dataSource.universeDao().findAllItems()
    }

    fun insert(name: String, fbdlId: Long) {
        val universe = Universe(null, name, fbdlId)
        dataSource.universeDao().insertItem(universe)
    }

    fun remove(universe: Universe) {
        dataSource.universeDao().deleteItem(universe)
    }
}