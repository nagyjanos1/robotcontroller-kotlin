package com.example.robotcontroller.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.FbdlCommandItem

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

class FbdlCommandItemViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FbdlCommandItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FbdlCommandItemViewModel(
                AppDatabase.getInstance(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}