package com.example.robotcontroller.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.robotcontroller.data.AppDatabase

class GenericViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FbdlCommandItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FbdlCommandItemViewModel(
                AppDatabase.getInstance(context)
            ) as T
        }
        if (modelClass.isAssignableFrom(UniverseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UniverseViewModel(
                AppDatabase.getInstance(context)
            ) as T
        }
        if (modelClass.isAssignableFrom(LimitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LimitViewModel(
                AppDatabase.getInstance(context)
            ) as T
        }
        if (modelClass.isAssignableFrom(RuleBaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RuleBaseViewModel(
                AppDatabase.getInstance(context)
            ) as T
        }
        if (modelClass.isAssignableFrom(RuleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RuleBaseViewModel(
                AppDatabase.getInstance(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}