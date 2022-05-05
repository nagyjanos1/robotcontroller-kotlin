package com.example.robotcontroller.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.entities.FbdlCommandItem

class FbdlCommandItemViewModel(val dataSource: AppDatabase) : ViewModel() {
    fun getById(itemId: Long): FbdlCommandItem? {
       return dataSource.fbdlCommandItemDao().findItemById(itemId)
    }

    fun getAll(): LiveData<List<FbdlCommandItem>> {
        return dataSource.fbdlCommandItemDao().findAllItems()
    }

    fun insert(name: String, commandText: String) {
        val fbdl = FbdlCommandItem(null, name, commandText, false)
        dataSource.fbdlCommandItemDao().insertItem(fbdl)
    }

    fun remove(fbdl: FbdlCommandItem) {
        dataSource.fbdlCommandItemDao().deleteItem(fbdl)
    }
}

