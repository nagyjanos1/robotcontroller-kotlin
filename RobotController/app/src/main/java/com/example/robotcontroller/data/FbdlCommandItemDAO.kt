package com.example.robotcontroller.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FbdlCommandItemDAO {
    @Query("SELECT * FROM fbdlcommanditem")
    fun findAllItems(): LiveData<List<FbdlCommandItem>>

    @Insert
    fun insertItem(item: FbdlCommandItem): Long

    @Delete
    fun deleteItem(item: FbdlCommandItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItem(item: FbdlCommandItem)

    @Query("SELECT * FROM fbdlcommanditem WHERE itemId = :currentFbdlCommandId")
    fun findItemById(currentFbdlCommandId: Long): FbdlCommandItem?
}