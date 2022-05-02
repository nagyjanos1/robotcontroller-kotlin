package com.example.robotcontroller.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.robotcontroller.data.daos.*

@Database(entities = [
    FbdlCommandItem::class,
    Universe::class,
    Limit::class,
    RuleBase::class,
    Rule::class,
 ], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fbdlCommandItemDao(): FbdlCommandItemDAO
    abstract fun universeDao(): UniverseDAO
    abstract fun limitsDao(): LimitsDAO
    abstract fun ruleBaseDao(): RuleBaseDAO
    abstract fun ruleDao(): RuleDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "fbdlcommands.db"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}